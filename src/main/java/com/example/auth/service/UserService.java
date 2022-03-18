package com.example.auth.service;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) throws UserBadRequestException {
        User persistedUser = userRepository.save(user);
        if (persistedUser.getUsername() == null || persistedUser.getPassword() == null || persistedUser.getEmail() == null || persistedUser.getEnabled() == null) {
            throw new UserBadRequestException("Данные некорректны");
        }
        return persistedUser;
    }

    public User loginWithPassword(String username, String password) throws UserNotFoundException {
        User persistedUser = userRepository.findByUsernamePassword(username, password);
        if (persistedUser == null) {
            throw new UserNotFoundException();
        }
        return persistedUser;
    }
}
