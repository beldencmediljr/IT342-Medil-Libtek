package edu.cit.medil.libtek.features.verification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.features.user.UserRepository;

@RestController
@RequestMapping("/api/verifications")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Verification>> getAllVerifications() {
        return ResponseEntity.ok(verificationRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> submitVerification(@RequestBody Verification verification) {
        Map<String, Object> response = new HashMap<>();
        try {
            verification.setStatus("Pending Review");
            Verification saved = verificationRepository.save(verification);
            response.put("success", true);
            response.put("data", saved);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Verification> updateVerificationStatus(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        Verification verification = verificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Verification log target missing"));
        
        if (payload.containsKey("status")) {
            String status = payload.get("status");
            verification.setStatus(status);
            
            // Sync user's verification state based on approval/rejection status
            userRepository.findByEmail(verification.getEmail()).ifPresent(user -> {
                if ("approved".equalsIgnoreCase(status)) {
                    user.setIsVerified(true);
                } else if ("rejected".equalsIgnoreCase(status)) {
                    user.setIsVerified(false);
                }
                userRepository.save(user);
            });
        }
        if (payload.containsKey("rejectionReason")) {
            verification.setRejectionReason(payload.get("rejectionReason"));
        }
        
        return ResponseEntity.ok(verificationRepository.save(verification));
    }
}