package edu.cit.medil.libtek.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.Fine;

public interface FineRepository extends JpaRepository<Fine, Long> {
}