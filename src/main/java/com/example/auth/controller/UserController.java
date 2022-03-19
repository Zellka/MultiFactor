package com.example.auth.controller;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.EmailPassword;
import com.example.auth.model.User;
import com.example.auth.model.UserLogin;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.EmailSenderService;
import com.example.auth.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

    private final String password = RandomStringUtils.randomNumeric(6);

    @PostMapping("/registration")
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            return new ResponseEntity(userService.createUser(user), HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/password")
    public ResponseEntity loginWithPassword(@RequestBody UserLogin user) {
        try {
            return new ResponseEntity(userService.loginWithPassword(user.getUsername(), user.getPassword()), HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/auth/send-email")
    public ResponseEntity sendEmail(@RequestBody User user) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("Подтвердите вход в аккаунт");
            mailMessage.setFrom("ilona.hackathon.spring@gmail.com");
            mailMessage.setText("Здравствуйте, " + user.getUsername() + ". Ваш код: " + password);

            emailSenderService.sendEmail(mailMessage);
            return new ResponseEntity("Письмо отправлено", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/email")
    public ResponseEntity loginWithEmail(@RequestBody EmailPassword emailPassword) {
        if (Objects.equals(emailPassword.getPassword(), password)) {
            return new ResponseEntity("Авторизация прошла успешно",HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        List<User> listUser = userService.getUsers();
        if (listUser.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return new ResponseEntity<>(listUser, HttpStatus.OK);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity deleteUser(@PathVariable("id") Integer id) {
        try {
            userService.deleteUser(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
