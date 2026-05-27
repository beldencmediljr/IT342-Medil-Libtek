package edu.cit.medil.libtek.features.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.features.reservation.Reservation;
import edu.cit.medil.libtek.features.reservation.ReservationRepository;
import edu.cit.medil.libtek.features.verification.Verification;
import edu.cit.medil.libtek.features.verification.VerificationRepository;
import edu.cit.medil.libtek.features.fine.Fine;
import edu.cit.medil.libtek.features.fine.FineRepository;

@RestController
@RequestMapping("/api/v1/user/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private FineRepository fineRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMobileProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reservation> allUserReservations = reservationRepository.findByStudentName(user.getFullName());
        List<Reservation> activeReservations = reservationRepository.findByStudentNameAndStatus(user.getFullName(), "ACTIVE");

        Optional<Verification> verificationOpt = verificationRepository.findTopByEmailOrderByIdDesc(user.getEmail());
        String verificationStatus = "Not Verified";
        
        if (user.getIsVerified() != null && user.getIsVerified()) {
            verificationStatus = "Verified";
        } else if (verificationOpt.isPresent()) {
            String vStatus = verificationOpt.get().getStatus();
            if ("pending review".equalsIgnoreCase(vStatus) || "pending".equalsIgnoreCase(vStatus)) {
                verificationStatus = "Pending Review";
            } else if ("rejected".equalsIgnoreCase(vStatus)) {
                verificationStatus = "Rejected";
            } else if ("approved".equalsIgnoreCase(vStatus)) {
                verificationStatus = "Verified";
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("activeBookings", activeReservations.size());
        data.put("studyTimeHours", 12); 
        data.put("booksRead", Math.max(0, allUserReservations.size() - activeReservations.size())); 
        data.put("phone", user.getPhoneNumber() != null ? user.getPhoneNumber() : "+63"); 
        data.put("verificationStatus", verificationStatus);
        data.put("rejectionReason", (verificationOpt.isPresent() && "rejected".equalsIgnoreCase(verificationOpt.get().getStatus())) ? verificationOpt.get().getRejectionReason() : null);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateProfile(Authentication authentication, @RequestBody Map<String, String> payload) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (payload.containsKey("fullName")) {
            user.setFullName(payload.get("fullName"));
        }
        if (payload.containsKey("phone")) {
            user.setPhoneNumber(payload.get("phone"));
        }
        
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(Authentication authentication, @RequestBody Map<String, String> payload) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Map<String, Object> response = new HashMap<>();

        if(passwordEncoder.matches(payload.get("oldPassword"), user.getPasswordHash())) {
            user.setPasswordHash(passwordEncoder.encode(payload.get("newPassword")));
            userRepository.save(user);
            response.put("success", true);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Incorrect old password");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/reservations")
    public ResponseEntity<List<Reservation>> getUserReservations(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reservation> userReservations = reservationRepository.findByStudentName(user.getFullName());
        return ResponseEntity.ok(userReservations);
    }

    @GetMapping("/fines")
    public ResponseEntity<List<Fine>> getUserFines(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Fine> userFines = fineRepository.findByStudentName(user.getFullName());
        return ResponseEntity.ok(userFines);
    }
}