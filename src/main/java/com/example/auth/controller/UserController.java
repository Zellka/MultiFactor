package com.example.auth.controller;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.User;
import com.example.auth.model.UserLogin;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/registration")
    public ResponseEntity createUser(@RequestBody User user) {
        try {
            return new ResponseEntity(userService.createUser(user), HttpStatus.CREATED);
        } catch (UserBadRequestException e) {
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
}
