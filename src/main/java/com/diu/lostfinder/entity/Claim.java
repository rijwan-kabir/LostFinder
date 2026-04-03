package com.diu.lostfinder.entity;

import java.time.LocalDateTime;

public class Claim {

    private Long id;
    private Item item;
    private User claimant;
    private String description;
    private String proof;
    private ClaimStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private User processedBy;
    private String adminNotes;

    public enum ClaimStatus {
        PENDING, APPROVED, REJECTED
    }

    // Constructors
    public Claim() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }

    public User getClaimant() { return claimant; }
    public void setClaimant(User claimant) { this.claimant = claimant; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getProof() { return proof; }
    public void setProof(String proof) { this.proof = proof; }

    public ClaimStatus getStatus() { return status; }
    public void setStatus(ClaimStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public User getProcessedBy() { return processedBy; }
    public void setProcessedBy(User processedBy) { this.processedBy = processedBy; }

    public String getAdminNotes() { return adminNotes; }
    public void setAdminNotes(String adminNotes) { this.adminNotes = adminNotes; }
}