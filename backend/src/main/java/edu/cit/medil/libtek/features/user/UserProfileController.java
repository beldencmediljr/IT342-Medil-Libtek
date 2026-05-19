package edu.cit.medil.libtek.features.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

@RestController
@RequestMapping("/api/v1/user/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMobileProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Reservation> allUserReservations = reservationRepository.findByStudentName(user.getFullName());
        List<Reservation> activeReservations = reservationRepository.findByStudentNameAndStatus(user.getFullName(), "ACTIVE");

        String verificationStatus = (user.getIsVerified() != null && user.getIsVerified()) ? "Verified" : "Pending";

        Map<String, Object> data = new HashMap<>();
        data.put("activeBookings", activeReservations.size());
        data.put("studyTimeHours", 12); 
        data.put("booksRead", Math.max(0, allUserReservations.size() - activeReservations.size())); 
        data.put("phone", user.getPhoneNumber() != null ? user.getPhoneNumber() : "+63"); 
        data.put("verificationStatus", verificationStatus);

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

    // NEW: Real functional Change Password Endpoint
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
}