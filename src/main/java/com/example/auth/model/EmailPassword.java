package com.example.auth.model;

public class EmailPassword {
    private String password;

    public EmailPassword() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public EmailPassword(String password) {
        this.password = password;
    }
}

