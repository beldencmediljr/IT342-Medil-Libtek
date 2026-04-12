package edu.cit.medil.libtek.controller;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.model.LibraryVisit;
import edu.cit.medil.libtek.repository.LibraryVisitRepository;

@RestController
@RequestMapping("/api/scanner")
@CrossOrigin(origins = "*")
public class ScannerController {

    @Autowired
    private LibraryVisitRepository visitRepository;

    @PostMapping("/scan")
    public LibraryVisit processScan(@RequestBody Map<String, String> payload) {
        String studentId = payload.get("studentId");
        
        Optional<LibraryVisit> activeVisit = visitRepository.findByStudentIdAndStatus(studentId, "IN_LIBRARY");
        
        if (activeVisit.isPresent()) {
            LibraryVisit visit = activeVisit.get();
            visit.setCheckOutTime(LocalDateTime.now());
            visit.setStatus("COMPLETED");
            return visitRepository.save(visit);
        } else {
            LibraryVisit newVisit = new LibraryVisit();
            newVisit.setStudentId(studentId);
            newVisit.setCheckInTime(LocalDateTime.now());
            newVisit.setStatus("IN_LIBRARY");
            return visitRepository.save(newVisit);
        }
    }
}