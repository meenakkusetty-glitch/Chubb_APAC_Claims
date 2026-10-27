package com.chubb.claims.events;
import java.math.BigDecimal;
import java.time.Instant;
public record ClaimEvent(String eventId, String type, String claimId, String status,
 BigDecimal estimatedLiability, String assignedStaff, String decision,
 BigDecimal settlementAmount, Instant occurredAt) {}
