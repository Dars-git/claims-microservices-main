package io.claimflow.intake.model;

import io.claimflow.contracts.model.CoverageCategory;
import io.claimflow.contracts.model.ProcessingState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

/** Database-backed aggregate for one insurance claim. */
@Entity
@Table(name = "claims")
public class ClaimRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String claimNumber;

    @Column(nullable = false)
    private String policyNumber;

    @Column(nullable = false)
    private String claimantName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CoverageCategory type;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProcessingState status;

    private String rejectionReason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected ClaimRecord() {
        // Required by JPA.
    }

    public ClaimRecord(String reference, String policy, String claimant,
                       CoverageCategory category, String details, BigDecimal requestedAmount) {
        claimNumber = reference;
        policyNumber = policy;
        claimantName = claimant;
        type = category;
        description = details;
        amount = requestedAmount;
        status = ProcessingState.SUBMITTED;
    }

    @PrePersist
    void markCreated() {
        Instant currentTime = Instant.now();
        createdAt = currentTime;
        updatedAt = currentTime;
    }

    @PreUpdate
    void markUpdated() {
        updatedAt = Instant.now();
    }

    public void accept() {
        status = ProcessingState.APPROVED;
    }

    public void decline(String explanation) {
        status = ProcessingState.REJECTED;
        rejectionReason = explanation;
    }

    public boolean isPending() {
        return status == ProcessingState.SUBMITTED;
    }

    public Long id() {
        return id;
    }

    public String reference() {
        return claimNumber;
    }

    public String policy() {
        return policyNumber;
    }

    public String claimant() {
        return claimantName;
    }

    public CoverageCategory category() {
        return type;
    }

    public String details() {
        return description;
    }

    public BigDecimal requestedAmount() {
        return amount;
    }

    public ProcessingState state() {
        return status;
    }

    public String declineReason() {
        return rejectionReason;
    }

    public Instant createdTime() {
        return createdAt;
    }

    public Instant modifiedTime() {
        return updatedAt;
    }
}
