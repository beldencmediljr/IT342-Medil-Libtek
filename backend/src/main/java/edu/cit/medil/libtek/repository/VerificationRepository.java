package edu.cit.medil.libtek.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.Verification;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
}