package edu.cit.medil.libtek.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.cit.medil.libtek.dto.DashboardSummaryDTO;
import edu.cit.medil.libtek.model.Fine;
import edu.cit.medil.libtek.model.Reservation;
import edu.cit.medil.libtek.model.Verification;
import edu.cit.medil.libtek.repository.FineRepository;
import edu.cit.medil.libtek.repository.LibraryVisitRepository;
import edu.cit.medil.libtek.repository.ReservationRepository;
import edu.cit.medil.libtek.repository.VerificationRepository;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private VerificationRepository verificationRepository;

    @Autowired
    private FineRepository fineRepository;

    @Autowired
    private LibraryVisitRepository visitRepository;

    @GetMapping("/summary")
    public DashboardSummaryDTO getSummary() {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();

        List<Reservation> activeReservations = reservationRepository.findByStatus("ACTIVE");
        summary.setActiveReservations(activeReservations.size());

        int maxCap = 100;
        long actualOccupancy = visitRepository.countByStatus("IN_LIBRARY");
        summary.setMaxCapacity(maxCap);
        summary.setCurrentOccupancy((int) actualOccupancy);

        List<Verification> allVerifications = verificationRepository.findAll();
        List<Verification> pendingVerifications = allVerifications.stream()
                .filter(v -> "pending".equalsIgnoreCase(v.getStatus()))
                .collect(Collectors.toList());
        summary.setPendingVerificationsCount(pendingVerifications.size());
        summary.setPendingVerifications(pendingVerifications.stream().limit(3).collect(Collectors.toList()));

        List<Fine> allFines = fineRepository.findAll();
        List<Fine> unpaidFines = allFines.stream()
                .filter(f -> "unpaid".equalsIgnoreCase(f.getStatus()))
                .collect(Collectors.toList());
        summary.setOverdueItemsCount(unpaidFines.size());
        double totalFines = unpaidFines.stream().mapToDouble(Fine::getAmount).sum();
        summary.setOverdueFinesTotal(totalFines);

        List<Reservation> allReservations = reservationRepository.findAll();
        summary.setRecentActivities(allReservations.stream().limit(5).collect(Collectors.toList()));

        return summary;
    }
}