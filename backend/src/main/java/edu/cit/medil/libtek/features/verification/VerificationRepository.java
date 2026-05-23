package edu.cit.medil.libtek.features.verification;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
    Optional<Verification> findTopByEmailOrderByIdDesc(String email);
}