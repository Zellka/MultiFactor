package com.example.auth.controller;

import com.example.auth.exception.UserBadRequestException;
import com.example.auth.exception.UserNotFoundException;
import com.example.auth.model.EmailCode;
import com.example.auth.model.User;
import com.example.auth.model.UserLogin;
import com.example.auth.model.ValidateCodeDto;
import com.example.auth.model.Validation;
import com.example.auth.repository.UserRepository;
import com.example.auth.service.UserService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoogleAuthenticator gAuth;


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

    @PostMapping("/send-email")
    public ResponseEntity sendEmail(@RequestBody User user) {
        try {
            userService.sendEmail(user);
            return new ResponseEntity("Письмо отправлено", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/email")
    public ResponseEntity loginWithEmail(@RequestBody EmailCode emailCode) {
        if (userService.loginWithEmail(emailCode)) {
            return new ResponseEntity("Авторизация прошла успешно", HttpStatus.OK);
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/generate-qr/{email}")
    public ResponseEntity generateQr(@PathVariable String email, HttpServletResponse response) throws WriterException, IOException {
        try {
            if (userRepository.findByEmail(email) != null) {
                final GoogleAuthenticatorKey key = gAuth.createCredentials(email);
                QRCodeWriter qrCodeWriter = new QRCodeWriter();
                String otpAuthURL = GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("MFA", email, key);
                BitMatrix bitMatrix = qrCodeWriter.encode(otpAuthURL, BarcodeFormat.QR_CODE, 200, 200);
                ServletOutputStream outputStream = response.getOutputStream();
                MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
                outputStream.close();
                return new ResponseEntity(HttpStatus.OK);
            } else throw new UserBadRequestException("Такой почты не существует");
        } catch (UserBadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth/validate-key")
    public Validation loginWithQr(@RequestBody ValidateCodeDto body) {
        return new Validation(gAuth.authorizeUser(body.getUsername(), body.getCode()));
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
