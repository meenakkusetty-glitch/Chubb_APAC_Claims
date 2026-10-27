package com.chubb.claims.claim;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant;
@Entity @Table(name="claims") public class Claim {
 @Id private String id; private String claimantName; @Column(length=2000) private String incidentDescription;
 @Enumerated(EnumType.STRING) private ClaimStatus status; private BigDecimal estimatedLiability; private String assignedStaff; private String decision; private BigDecimal settlementAmount; private Instant createdAt;
 protected Claim() {}
 public Claim(String id,String claimantName,String incidentDescription,BigDecimal estimatedLiability){this.id=id;this.claimantName=claimantName;this.incidentDescription=incidentDescription;this.estimatedLiability=estimatedLiability;this.status=ClaimStatus.SUBMITTED;this.createdAt=Instant.now();}
 public String getId(){return id;} public String getClaimantName(){return claimantName;} public String getIncidentDescription(){return incidentDescription;} public ClaimStatus getStatus(){return status;} public BigDecimal getEstimatedLiability(){return estimatedLiability;} public String getAssignedStaff(){return assignedStaff;} public String getDecision(){return decision;} public BigDecimal getSettlementAmount(){return settlementAmount;} public Instant getCreatedAt(){return createdAt;}
 public void assign(String staff){this.assignedStaff=staff; if(status==ClaimStatus.SUBMITTED)status=ClaimStatus.UNDER_REVIEW;}
 public void requestInformation(){if(status!=ClaimStatus.UNDER_REVIEW)throw new IllegalStateException("Information can only be requested during review");status=ClaimStatus.AWAITING_INFORMATION;}
 public void provideInformation(){if(status!=ClaimStatus.AWAITING_INFORMATION)throw new IllegalStateException("Claim is not awaiting information");status=ClaimStatus.UNDER_REVIEW;}
 public void decide(String decision,BigDecimal amount){if(status!=ClaimStatus.UNDER_REVIEW)throw new IllegalStateException("Only claims under review can be decided"); if("SETTLED".equals(decision)){if(amount==null||amount.signum()<0)throw new IllegalArgumentException("A non-negative settlement amount is required");status=ClaimStatus.SETTLED;settlementAmount=amount;}else if("REJECTED".equals(decision)){if(amount!=null)throw new IllegalArgumentException("Settlement amount must be omitted for rejected claims");status=ClaimStatus.REJECTED;}else throw new IllegalArgumentException("Decision must be SETTLED or REJECTED");this.decision=decision;}
}
