package edu.cit.medil.libtek.features.reservation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String studentName;
    private String resourceType;
    private String resourceName;
    private String reservationDate;
    private String status;

    public Long getId() { 
        return id; 
    }
    
    public void setId(Long id) { 
        this.id = id; 
    }
    
    public String getStudentName() { 
        return studentName; 
    }
    
    public void setStudentName(String studentName) { 
        this.studentName = studentName; 
    }
    
    public String getResourceType() { 
        return resourceType; 
    }
    
    public void setResourceType(String resourceType) { 
        this.resourceType = resourceType; 
    }
    
    public String getResourceName() { 
        return resourceName; 
    }
    
    public void setResourceName(String resourceName) { 
        this.resourceName = resourceName; 
    }
    
    public String getReservationDate() { 
        return reservationDate; 
    }
    
    public void setReservationDate(String reservationDate) { 
        this.reservationDate = reservationDate; 
    }
    
    public String getStatus() { 
        return status; 
    }
    
    public void setStatus(String status) { 
        this.status = status; 
    }
}