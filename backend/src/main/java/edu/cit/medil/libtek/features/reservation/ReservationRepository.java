package edu.cit.medil.libtek.features.reservation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    
    // Existing method
    List<Reservation> findByStatus(String status);
    
    // THE FIX: Uses "StudentName" exactly as it is typed in your Reservation.java entity
    List<Reservation> findByStudentName(String studentName);
    List<Reservation> findByStudentNameAndStatus(String studentName, String status);
}