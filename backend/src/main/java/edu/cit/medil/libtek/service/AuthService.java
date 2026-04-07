package edu.cit.medil.libtek.service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import edu.cit.medil.libtek.model.User;
import edu.cit.medil.libtek.repository.UserRepository;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Map<String, Object> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            User user = userOpt.get();
            Map<String, Object> data = new HashMap<>();
            
            // Securing user payload 
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("email", user.getEmail());
            userData.put("full_name", user.getFullName());
            userData.put("role", user.getRole());
            userData.put("id_image_url", user.getIdImageUrl());
            userData.put("is_verified", user.getIsVerified());
            
            data.put("user", userData);
            data.put("accessToken", "jwt-token-placeholder");
            data.put("refreshToken", "refresh-token-placeholder");
            
            return data;
        }
        
        return null; // Signals authentication failure
    }

    public boolean registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return false; // Signals email already exists
        }
        
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        
        user.setPasswordHash(passwordEncoder.encode(user.getPassword()));
        user.setIsVerified(false);
        userRepository.save(user);
        
        return true;
    }
}