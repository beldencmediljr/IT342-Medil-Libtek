package edu.cit.medil.libtek.features.dashboard;

import edu.cit.medil.libtek.features.fine.Fine;
import edu.cit.medil.libtek.features.fine.FineRepository;
import edu.cit.medil.libtek.features.libraryvisit.LibraryVisitRepository;
import edu.cit.medil.libtek.features.reservation.Reservation;
import edu.cit.medil.libtek.features.reservation.ReservationRepository;
import edu.cit.medil.libtek.features.user.User;
import edu.cit.medil.libtek.features.user.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/dashboard")
@CrossOrigin(origins = "*")
public class UserDashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LibraryVisitRepository visitRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getMobileDashboard(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        long actualOccupancy = visitRepository.countByStatus("IN_LIBRARY");
        int maxCapacity = 100;

        // THE FIX: Fetching records using the user's Full Name to match the database records
        List<Fine> userFines = fineRepository.findByStudentNameAndStatus(user.getFullName(), "unpaid");
        double totalFines = userFines.stream().mapToDouble(Fine::getAmount).sum();
        boolean hasFine = totalFines > 0;

        // THE FIX: Fetching reservations using the user's Full Name
        List<Reservation> activeReservations = reservationRepository.findByStudentNameAndStatus(user.getFullName(), "ACTIVE");
        int activeBooksCount = activeReservations.size(); 

        Map<String, Object> data = new HashMap<>();
        data.put("currentOccupancy", actualOccupancy);
        data.put("maxCapacity", maxCapacity);
        data.put("hasFine", hasFine);
        data.put("fineAmount", totalFines);
        data.put("activeBooks", activeBooksCount);
        data.put("boothsToday", 0); 
        data.put("hoursThisWeek", 0); 
        
        if (!activeReservations.isEmpty()) {
            Reservation next = activeReservations.get(0);
            Map<String, String> upcoming = new HashMap<>();
            upcoming.put("location", next.getResourceName() != null ? next.getResourceName() : "Reserved Resource"); 
            upcoming.put("date", next.getReservationDate() != null ? next.getReservationDate() : "Today");
            upcoming.put("time", "Scheduled");
            upcoming.put("status", next.getStatus());
            data.put("upcomingReservation", upcoming);
        } else {
            data.put("upcomingReservation", null);
        }

        data.put("recentActivity", new ArrayList<>());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}