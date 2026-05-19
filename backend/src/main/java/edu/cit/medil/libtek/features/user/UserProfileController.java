package edu.cit.medil.libtek.features.user;

import edu.cit.medil.libtek.features.reservation.Reservation;
import edu.cit.medil.libtek.features.reservation.ReservationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/profile")
@CrossOrigin(origins = "*")
public class UserProfileController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMobileProfile(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // THE FIX: Fetching records using the user's Full Name to match your entities
        List<Reservation> allUserReservations = reservationRepository.findByStudentName(user.getFullName());
        List<Reservation> activeReservations = reservationRepository.findByStudentNameAndStatus(user.getFullName(), "ACTIVE");

        String verificationStatus = (user.getIsVerified() != null && user.getIsVerified()) ? "Verified" : "Pending";

        Map<String, Object> data = new HashMap<>();
        data.put("activeBookings", activeReservations.size());
        data.put("studyTimeHours", 0); 
        data.put("booksRead", Math.max(0, allUserReservations.size() - activeReservations.size())); 
        data.put("phone", "+63 912 345 6789"); 
        data.put("verificationStatus", verificationStatus);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}