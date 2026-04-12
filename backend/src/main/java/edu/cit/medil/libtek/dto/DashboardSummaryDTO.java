package edu.cit.medil.libtek.dto;

import java.util.List;

import edu.cit.medil.libtek.model.Reservation;
import edu.cit.medil.libtek.model.Verification;

public class DashboardSummaryDTO {
    private long activeReservations;
    private long pendingVerificationsCount;
    private long overdueItemsCount;
    private double overdueFinesTotal;
    private List<Verification> pendingVerifications;
    private List<Reservation> recentActivities;
    
    // NEW: Real occupancy tracking
    private int currentOccupancy;
    private int maxCapacity;

    public long getActiveReservations() { return activeReservations; }
    public void setActiveReservations(long activeReservations) { this.activeReservations = activeReservations; }
    public long getPendingVerificationsCount() { return pendingVerificationsCount; }
    public void setPendingVerificationsCount(long pendingVerificationsCount) { this.pendingVerificationsCount = pendingVerificationsCount; }
    public long getOverdueItemsCount() { return overdueItemsCount; }
    public void setOverdueItemsCount(long overdueItemsCount) { this.overdueItemsCount = overdueItemsCount; }
    public double getOverdueFinesTotal() { return overdueFinesTotal; }
    public void setOverdueFinesTotal(double overdueFinesTotal) { this.overdueFinesTotal = overdueFinesTotal; }
    public List<Verification> getPendingVerifications() { return pendingVerifications; }
    public void setPendingVerifications(List<Verification> pendingVerifications) { this.pendingVerifications = pendingVerifications; }
    public List<Reservation> getRecentActivities() { return recentActivities; }
    public void setRecentActivities(List<Reservation> recentActivities) { this.recentActivities = recentActivities; }
    
    // NEW Getters and Setters
    public int getCurrentOccupancy() { return currentOccupancy; }
    public void setCurrentOccupancy(int currentOccupancy) { this.currentOccupancy = currentOccupancy; }
    public int getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(int maxCapacity) { this.maxCapacity = maxCapacity; }
}