package edu.cit.medil.libtek.features.reservation;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(String status);
}