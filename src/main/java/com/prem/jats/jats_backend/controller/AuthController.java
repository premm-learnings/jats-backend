package com.prem.jats.jats_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.prem.jats.jats_backend.dto.LoginRequest;
import com.prem.jats.jats_backend.dto.LoginResponse;
import com.prem.jats.jats_backend.dto.RegisterRequest;
import com.prem.jats.jats_backend.dto.UserResponse;
import com.prem.jats.jats_backend.service.AuthService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest request) {
       UserResponse response = authService.register(request);
       return new ResponseEntity<>(response,HttpStatus.CREATED);
    }
     
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
