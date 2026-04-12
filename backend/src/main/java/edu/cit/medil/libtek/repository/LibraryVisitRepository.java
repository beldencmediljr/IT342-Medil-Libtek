package edu.cit.medil.libtek.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.LibraryVisit;

public interface LibraryVisitRepository extends JpaRepository<LibraryVisit, Long> {
    Optional<LibraryVisit> findByStudentIdAndStatus(String studentId, String status);
    long countByStatus(String status);
}