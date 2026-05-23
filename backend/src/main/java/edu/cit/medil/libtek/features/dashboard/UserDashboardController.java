package edu.cit.medil.libtek.features.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.features.fine.Fine;
import edu.cit.medil.libtek.features.fine.FineRepository;
import edu.cit.medil.libtek.features.libraryvisit.LibraryVisitRepository;
import edu.cit.medil.libtek.features.reservation.Reservation;
import edu.cit.medil.libtek.features.reservation.ReservationRepository;
import edu.cit.medil.libtek.features.user.User;
import edu.cit.medil.libtek.features.user.UserRepository;

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

        List<Fine> userFines = fineRepository.findByStudentNameAndStatus(user.getFullName(), "unpaid");
        double totalFines = userFines.stream().mapToDouble(Fine::getAmount).sum();
        boolean hasFine = totalFines > 0;

        List<Reservation> activeReservations = reservationRepository.findByStudentNameAndStatus(user.getFullName(), "ACTIVE");
        
        int activeBooksCount = 0;
        int activeBoothsCount = 0;
        for (Reservation res : activeReservations) {
            if ("BOOTH".equalsIgnoreCase(res.getResourceType())) {
                activeBoothsCount++;
            } else {
                activeBooksCount++;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("currentOccupancy", actualOccupancy);
        data.put("maxCapacity", maxCapacity);
        data.put("hasFine", hasFine);
        data.put("fineAmount", totalFines);
        data.put("activeBooks", activeBooksCount);
        data.put("boothsToday", activeBoothsCount); 
        data.put("hoursThisWeek", 12); 
        
        List<Map<String, String>> upcomingList = new ArrayList<>();
        for (Reservation next : activeReservations) {
            Map<String, String> upcoming = new HashMap<>();
            upcoming.put("location", next.getResourceName() != null ? next.getResourceName() : "Reserved Resource"); 
            upcoming.put("date", next.getReservationDate() != null ? next.getReservationDate() : "Today");
            upcoming.put("time", "Scheduled");
            upcoming.put("status", next.getStatus());
            upcomingList.add(upcoming);
        }
        data.put("upcomingReservation", upcomingList);

        List<Reservation> allUserReservations = reservationRepository.findByStudentName(user.getFullName());
        List<Map<String, String>> recentActivities = new ArrayList<>();
        for (Reservation res : allUserReservations) {
            Map<String, String> activity = new HashMap<>();
            String statusTitle = "Booked";
            if ("CANCELLED".equalsIgnoreCase(res.getStatus())) {
                statusTitle = "Cancelled";
            } else if ("COMPLETED".equalsIgnoreCase(res.getStatus())) {
                statusTitle = "Completed";
            }
            activity.put("title", statusTitle + " " + (res.getResourceType() != null ? res.getResourceType().toLowerCase() : "resource"));
            activity.put("subtitle", res.getResourceName() != null ? res.getResourceName() : "Library Resource");
            activity.put("timeAgo", res.getReservationDate() != null ? res.getReservationDate() : "Recently");
            activity.put("type", res.getResourceType() != null ? res.getResourceType() : "BOOK");
            recentActivities.add(activity);
        }
        data.put("recentActivity", recentActivities);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", data);

        return ResponseEntity.ok(response);
    }
}