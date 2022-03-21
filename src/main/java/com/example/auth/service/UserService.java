package com.example.auth.service;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.EmailCode;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    private final String code = RandomStringUtils.randomNumeric(6);

    private String emailUser;

    public User createUser(User user) throws UserBadRequestException {
        User emailUser = userRepository.findByEmail(user.getEmail());
        User nameUser = userRepository.findByUsername(user.getUsername());
        if (emailUser == null) {
            if (nameUser == null) {
                if (validateEmail(user.getEmail())) {
                    if (user.getPassword().length() >= 6 && user.getPassword().length() <= 30) {
                        return userRepository.save(user);
                    } else {
                        throw new UserBadRequestException("Пароль должен быть больше 5 и меньше 30 символов");
                    }
                } else {
                    throw new UserBadRequestException("Неверная почта");
                }
            } else {
                throw new UserBadRequestException("Данный логин уже занят");
            }
        }
        throw new UserBadRequestException("Данная почта уже занята");
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }

    public User loginWithPassword(String username, String password) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsernamePassword(username, password);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }
        return user.get();
    }

    public void sendEmail(User user) throws UserBadRequestException {
        User emailU = userRepository.findByEmail(user.getEmail());
        if (emailU != null) {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            emailUser = user.getEmail();
            mailMessage.setTo(emailUser);
            mailMessage.setSubject("Подтвердите вход в аккаунт");
            mailMessage.setFrom("hackathon.spring@gmail.com");
            mailMessage.setText("Здравствуйте, " + user.getUsername() + ". Ваш код: " + code);

            emailSenderService.sendEmail(mailMessage);
        } else throw new UserBadRequestException("Такой почты не найдено");
    }

    public Boolean loginWithEmail(EmailCode emailCode) {
        if (Objects.equals(emailUser, emailCode.getEmail())) {
            return Objects.equals(emailCode.getCode(), code);
        }
        return false;
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Integer id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
        } else {
            throw new UserNotFoundException();
        }
    }
}
