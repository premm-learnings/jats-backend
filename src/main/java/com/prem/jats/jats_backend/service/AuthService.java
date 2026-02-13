package com.prem.jats.jats_backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.prem.jats.jats_backend.dto.LoginRequest;
import com.prem.jats.jats_backend.dto.LoginResponse;
import com.prem.jats.jats_backend.dto.RegisterRequest;
import com.prem.jats.jats_backend.dto.UserResponse;
import com.prem.jats.jats_backend.entity.User;
import com.prem.jats.jats_backend.exception.EmailAlreadyExistsException;
import com.prem.jats.jats_backend.repository.UserRepository;
import com.prem.jats.jats_backend.security.JwtUtil;

@Service
public class AuthService {

     private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    

   private final JwtUtil jwtUtil;

public AuthService(UserRepository userRepository,
                   PasswordEncoder passwordEncoder,
                   JwtUtil jwtUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtUtil = jwtUtil;
}


    public UserResponse register(RegisterRequest request) {

        // 1. Check if email already exists
        if (userRepository.findByEmail(request.getEmail()) != null) {
            throw new EmailAlreadyExistsException("Email already registered");

        }

        // 2. Create User entity
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // 3. Save user
        User savedUser = userRepository.save(user);

        // 4. Map to response DTO
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());

        return response;
}
public LoginResponse login(LoginRequest request) {

    // 1️⃣ Find user by email
    User user = userRepository.findByEmail(request.getEmail());

    if (user == null) {
        throw new RuntimeException("Invalid email or password");
    }

    // 2️⃣ Verify password
    boolean passwordMatches =
            passwordEncoder.matches(request.getPassword(), user.getPassword());

    if (!passwordMatches) {
        throw new RuntimeException("Invalid email or password");
    }

    // 3️⃣ Generate JWT
    String token = jwtUtil.generateToken(user.getId(), user.getEmail());

    // 4️⃣ Build response
    LoginResponse response = new LoginResponse();
    response.setToken(token);
    response.setUserId(user.getId());
    response.setEmail(user.getEmail());

    return response;
}

}