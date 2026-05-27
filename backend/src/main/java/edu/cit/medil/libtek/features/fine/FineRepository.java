package edu.cit.medil.libtek.features.fine;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FineRepository extends JpaRepository<Fine, Long> {
    
    // THE FIX: Uses "StudentName" exactly as it is typed in your Fine.java entity
    List<Fine> findByStudentNameAndStatus(String studentName, String status);

    List<Fine> findByStudentName(String studentName);
}