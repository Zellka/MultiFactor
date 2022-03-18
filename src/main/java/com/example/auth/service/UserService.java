package com.example.auth.service;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(User user) throws UserBadRequestException {
        return userRepository.save(user);
    }

    public User loginWithPassword(String username, String password) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsernamePassword(username, password);
        if (!user.isPresent()) {
            throw new UserNotFoundException();
        }
        return user.get();
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
