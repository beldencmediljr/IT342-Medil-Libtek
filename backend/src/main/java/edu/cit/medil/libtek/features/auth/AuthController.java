package edu.cit.medil.libtek.features.auth;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.features.user.User;
import edu.cit.medil.libtek.features.user.UserRepository;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> data = new HashMap<>();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("full_name", user.getFullName());
            userData.put("role", user.getRole());
            userData.put("id_image_url", user.getIdImageUrl());
            userData.put("is_verified", user.getIsVerified());

            data.put("user", userData);
            // Use the user's email as the bearer token.
            // The UserIdAuthFilter reads this to look up the user on protected endpoints.
            // Replace with a real signed JWT when you add JWT support.
            data.put("accessToken", user.getEmail());
            data.put("refreshToken", user.getEmail());

            response.put("data", data);
            response.put("error", null);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("data", null);

            Map<String, Object> error = new HashMap<>();
            error.put("code", "AUTH-001");
            error.put("message", "Invalid credentials");
            error.put("details", null);

            response.put("error", error);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.status(401).body(response);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        String fullName = requestData.get("fullName");
        String password = requestData.get("password");
        String role = requestData.get("role");

        if (email == null || fullName == null || password == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("data", null);
            Map<String, Object> error = new HashMap<>();
            error.put("code", "AUTH-002");
            error.put("message", "Missing required fields");
            error.put("details", null);
            response.put("error", error);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(400).body(response);
        }

        if (userRepository.existsByEmail(email)) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("data", null);
            Map<String, Object> error = new HashMap<>();
            error.put("code", "AUTH-001");
            error.put("message", "Email already registered");
            error.put("details", null);
            response.put("error", error);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(409).body(response);
        }

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole((role != null && !role.isEmpty()) ? role : "USER");
        user.setIsVerified(false);

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", null);
        response.put("error", null);
        response.put("timestamp", LocalDateTime.now().toString());

        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        if (idToken == null || idToken.trim().isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("data", null);
            Map<String, Object> error = new HashMap<>();
            error.put("code", "AUTH-002");
            error.put("message", "Missing Google ID token");
            response.put("error", error);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(400).body(response);
        }

        try {
            // Call Google tokeninfo endpoint to verify token
            String url = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
            org.springframework.web.client.RestTemplate restTemplate = new org.springframework.web.client.RestTemplate();
            Map<String, Object> googleResponse = restTemplate.getForObject(url, Map.class);

            if (googleResponse == null || googleResponse.containsKey("error_description")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("data", null);
                Map<String, Object> error = new HashMap<>();
                error.put("code", "AUTH-001");
                error.put("message", "Invalid Google ID token");
                response.put("error", error);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.status(401).body(response);
            }

            String email = (String) googleResponse.get("email");
            String name = (String) googleResponse.get("name");

            if (email == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("data", null);
                Map<String, Object> error = new HashMap<>();
                error.put("code", "AUTH-001");
                error.put("message", "Email not provided by Google");
                response.put("error", error);
                response.put("timestamp", LocalDateTime.now().toString());
                return ResponseEntity.status(400).body(response);
            }

            // Find or register the user
            Optional<User> userOpt = userRepository.findByEmail(email);
            User user;
            if (userOpt.isPresent()) {
                user = userOpt.get();
            } else {
                user = new User();
                user.setEmail(email);
                user.setFullName(name != null ? name : email.split("@")[0]);
                user.setPasswordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString())); // set random password since it's OAuth
                user.setRole("USER");
                user.setIsVerified(false); // Google login does not bypass ID verification
                userRepository.save(user);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);

            Map<String, Object> data = new HashMap<>();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("full_name", user.getFullName());
            userData.put("role", user.getRole());
            userData.put("id_image_url", user.getIdImageUrl());
            userData.put("is_verified", user.getIsVerified());

            data.put("user", userData);
            data.put("accessToken", user.getEmail());
            data.put("refreshToken", user.getEmail());

            response.put("data", data);
            response.put("error", null);
            response.put("timestamp", LocalDateTime.now().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("data", null);
            Map<String, Object> error = new HashMap<>();
            error.put("code", "SYS-001");
            error.put("message", "Failed to verify Google token: " + e.getMessage());
            response.put("error", error);
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(500).body(response);
        }
    }
}