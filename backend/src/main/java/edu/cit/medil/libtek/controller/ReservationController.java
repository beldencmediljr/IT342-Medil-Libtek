package edu.cit.medil.libtek.controller;

import edu.cit.medil.libtek.model.Reservation;
import edu.cit.medil.libtek.repository.ReservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*")
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @GetMapping
    public List<Reservation> getReservations(@RequestParam String status) {
        return reservationRepository.findByStatus(status);
    }

    @PutMapping("/{id}/status")
    public Reservation updateStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Reservation res = reservationRepository.findById(id).orElseThrow();
        res.setStatus(payload.get("status"));
        return reservationRepository.save(res);
    }
}