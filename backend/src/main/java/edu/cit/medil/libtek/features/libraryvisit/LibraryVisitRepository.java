package edu.cit.medil.libtek.features.libraryvisit;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface LibraryVisitRepository extends JpaRepository<LibraryVisit, Long> {
    Optional<LibraryVisit> findByStudentIdAndStatus(String studentId, String status);
    long countByStatus(String status);
}