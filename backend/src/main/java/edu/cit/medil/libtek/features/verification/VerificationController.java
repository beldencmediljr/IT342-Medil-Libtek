package edu.cit.medil.libtek.features.verification;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
@CrossOrigin(origins = "*")
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public List<Verification> getAllVerifications() {
        return verificationRepository.findAll();
    }

    @PostMapping
    public Verification submitVerification(@RequestBody Verification verification) {
        verification.setStatus("pending");
        Verification saved = verificationRepository.save(verification);
        userRepository.findByEmail(verification.getEmail()).ifPresent(user -> {
            user.setIdImageUrl(verification.getIdImageUrl());
            userRepository.save(user);
        });
        return saved;
    }

    @PutMapping("/{id}/status")
    public Verification updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Verification verification = verificationRepository.findById(id).orElseThrow();
        String status = payload.get("status");
        verification.setStatus(status);
        verification.setRejectionReason(payload.get("rejectionReason"));
        Verification saved = verificationRepository.save(verification);
        
        if ("approved".equalsIgnoreCase(status)) {
            userRepository.findByEmail(verification.getEmail()).ifPresent(user -> {
                user.setIsVerified(true);
                userRepository.save(user);
            });
        } else if ("rejected".equalsIgnoreCase(status)) {
            userRepository.findByEmail(verification.getEmail()).ifPresent(user -> {
                user.setIsVerified(false);
                userRepository.save(user);
            });
        }
        return saved;
    }
}