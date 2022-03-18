package com.example.auth.exception;

public class UserBadRequestException extends Exception {
    public UserBadRequestException(String message) {
        super(message);
    }
}