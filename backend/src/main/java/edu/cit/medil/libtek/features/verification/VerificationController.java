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


@RestController
@RequestMapping("/api/verifications")
@CrossOrigin(origins = "*")
public class VerificationController {

    @Autowired
    private VerificationRepository verificationRepository;

    @GetMapping
    public List<Verification> getAllVerifications() {
        return verificationRepository.findAll();
    }

    @PostMapping
    public Verification submitVerification(@RequestBody Verification verification) {
        verification.setStatus("pending");
        return verificationRepository.save(verification);
    }

    @PutMapping("/{id}/status")
    public Verification updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Verification verification = verificationRepository.findById(id).orElseThrow();
        verification.setStatus(payload.get("status"));
        verification.setRejectionReason(payload.get("rejectionReason"));
        return verificationRepository.save(verification);
    }
}