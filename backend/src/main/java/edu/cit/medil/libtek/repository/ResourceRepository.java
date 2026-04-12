package edu.cit.medil.libtek.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.cit.medil.libtek.model.Resource;

public interface ResourceRepository extends JpaRepository<Resource, Long> {
    List<Resource> findByType(String type);
}