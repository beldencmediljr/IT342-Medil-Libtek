package edu.cit.medil.libtek.controller;

import edu.cit.medil.libtek.model.User;
import edu.cit.medil.libtek.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> payload) {
        if (userRepository.existsByEmail(payload.get("email"))) {
            return ResponseEntity.badRequest().body("Email already exists");
        }

        User user = new User();
        user.setFullName(payload.get("fullName"));
        user.setEmail(payload.get("email"));
        // Hash the password!
        user.setPasswordHash(passwordEncoder.encode(payload.get("password"))); 
        
        userRepository.save(user);
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        Optional<User> userOpt = userRepository.findByEmail(payload.get("email"));
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Compare raw password with hashed password
            if (passwordEncoder.matches(payload.get("password"), user.getPasswordHash())) {
                return ResponseEntity.ok("Login Successful! Simulated JWT Token 12345");
            }
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }
}