package edu.cit.medil.libtek.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStatus(String status);
}