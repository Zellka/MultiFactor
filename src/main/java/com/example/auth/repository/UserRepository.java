package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u where u.username = :username and u.password = :password")
    Optional<User> findByUsernamePassword(String username, String password);
}
