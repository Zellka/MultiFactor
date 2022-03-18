package com.example.auth.model;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.constraints.*;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @NotNull
    @Size(min = 2, max = 50)
    @Column(unique = true)
    private String username;
    @NotNull
    @Size(min = 6, max = 50)
    private String password;
    @NotNull
    @Email
    @Column(unique = true)
    private String email;
    @NotNull
    @Min(0)
    @Max(1)
    private Integer isEnabled;

    public User() {
    }

    public User(String username, String password, String email, Integer isEnabled) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.isEnabled = isEnabled;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Integer enabled) {
        isEnabled = enabled;
    }
}
