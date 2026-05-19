package edu.cit.medil.libtek.features.reservation;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public List<Reservation> getReservations(@RequestParam(required = false) String status) {
        if (status != null) {
            return reservationRepository.findByStatus(status);
        }
        return reservationRepository.findAll();
    }

    // NEW: Fixes "Failed to book resource" by accepting POST requests
    @PostMapping
    public Reservation createReservation(@RequestBody Reservation reservation) {
        if (reservation.getStatus() == null || reservation.getStatus().isEmpty()) {
            reservation.setStatus("ACTIVE");
        }
        return reservationRepository.save(reservation);
    }

    @PutMapping("/{id}/status")
    public Reservation updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Reservation res = reservationRepository.findById(id).orElseThrow();
        res.setStatus(payload.get("status"));
        return reservationRepository.save(res);
    }
}