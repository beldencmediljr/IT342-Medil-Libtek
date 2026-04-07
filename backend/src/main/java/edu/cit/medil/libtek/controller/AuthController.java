package edu.cit.medil.libtek.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.model.User;
import edu.cit.medil.libtek.service.AuthService;
import edu.cit.medil.libtek.util.ApiResponseFactory;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService; 

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Map<String, Object> authData = authService.authenticateUser(email, password);

        if (authData != null) {
            return ApiResponseFactory.success(authData, 200);
        } else {
            return ApiResponseFactory.error("AUTH-001", "Invalid credentials", 401);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        boolean isRegistered = authService.registerUser(user);

        if (isRegistered) {
            return ApiResponseFactory.success("User registered successfully", 201);
        } else {
            return ApiResponseFactory.error("AUTH-001", "Email already registered", 409);
        }
    }
}