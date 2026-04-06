package edu.cit.medil.libtek.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}