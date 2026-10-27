package com.chubb.claims.reporting;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "claim_projection")
public class ClaimProjection {
    @Id
    private String claimId;
    private String status;
    private BigDecimal estimatedLiability;

    protected ClaimProjection() {
    }

    public ClaimProjection(String id, String status, BigDecimal liability) {
        claimId = id;
        this.status = status;
        estimatedLiability = liability == null ? BigDecimal.ZERO : liability;
    }

    public String getClaimId() {
        return claimId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getEstimatedLiability() {
        return estimatedLiability;
    }

    public void update(String s, BigDecimal amount) {
        status = s;
        if (amount != null) estimatedLiability = amount;
    }
}
