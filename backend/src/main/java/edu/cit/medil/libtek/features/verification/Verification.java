package edu.cit.medil.libtek.features.verification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "verifications")
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private String studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String email;

    @Lob
    @Column(name = "id_image_url", columnDefinition = "LONGTEXT")
    private String idImageUrl;

    @Column(nullable = false)
    private String status = "Pending Review";

    @Column(name = "rejection_reason")
    private String rejectionReason;

    // Default Constructor
    public Verification() {}

    // Parameterized Constructor
    public Verification(String studentId, String studentName, String email, String idImageUrl, String status) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.email = email;
        this.idImageUrl = idImageUrl;
        this.status = status;
    }

    // Encapsulation Accessors
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdImageUrl() { return idImageUrl; }
    public void setIdImageUrl(String idImageUrl) { this.idImageUrl = idImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}