package edu.cit.medil.libtek.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.model.Fine;
import edu.cit.medil.libtek.repository.FineRepository;

@RestController
@RequestMapping("/api/fines")
@CrossOrigin(origins = "*")
public class FineController {

    @Autowired
    private FineRepository fineRepository;

    @GetMapping
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    @PostMapping
    public Fine issueFine(@RequestBody Fine fine) {
        fine.setStatus("unpaid");
        return fineRepository.save(fine);
    }

    @PutMapping("/{id}/clear")
    public Fine clearFine(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Fine fine = fineRepository.findById(id).orElseThrow();
        fine.setStatus("cleared");
        fine.setReceiptNumber(payload.get("receiptNumber"));
        fine.setNotes(payload.get("notes"));
        return fineRepository.save(fine);
    }
}