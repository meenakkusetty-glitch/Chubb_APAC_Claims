package com.chubb.claims.claim;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ClaimLifecycleTest {
    private Claim submittedClaim() {
        return new Claim("claim-1", "Alex Smith", "Vehicle damage", new BigDecimal("12500.00"));
    }

    @Test
    void newClaimStartsSubmitted() {
        Claim claim = submittedClaim();
        assertEquals(ClaimStatus.SUBMITTED, claim.getStatus());
        assertEquals(new BigDecimal("12500.00"), claim.getEstimatedLiability());
        assertNotNull(claim.getCreatedAt());
    }

    @Test
    void assignmentMovesSubmittedClaimToReview() {
        Claim claim = submittedClaim();
        claim.assign("Taylor Lee");
        assertEquals("Taylor Lee", claim.getAssignedStaff());
        assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());
    }

    @Test
    void informationRequestMustBeFollowedByInformationBeforeDecision() {
        Claim claim = submittedClaim();
        claim.assign("Taylor Lee");
        claim.requestInformation();
        assertEquals(ClaimStatus.AWAITING_INFORMATION, claim.getStatus());
        assertThrows(IllegalStateException.class, () -> claim.decide("REJECTED", null));
        claim.provideInformation();
        assertEquals(ClaimStatus.UNDER_REVIEW, claim.getStatus());
    }

    @Test
    void settlementRequiresNonNegativeAmountAndEndsLifecycle() {
        Claim claim = submittedClaim();
        claim.assign("Taylor Lee");
        claim.decide("SETTLED", new BigDecimal("11000.00"));
        assertEquals(ClaimStatus.SETTLED, claim.getStatus());
        assertEquals(new BigDecimal("11000.00"), claim.getSettlementAmount());
        assertThrows(IllegalStateException.class, () -> claim.requestInformation());
    }

    @Test
    void rejectionMustNotHaveSettlementAmount() {
        Claim claim = submittedClaim();
        claim.assign("Taylor Lee");
        assertThrows(IllegalArgumentException.class,
                () -> claim.decide("REJECTED", new BigDecimal("10.00")));
        claim.decide("REJECTED", null);
        assertEquals(ClaimStatus.REJECTED, claim.getStatus());
        assertNull(claim.getSettlementAmount());
    }

    @Test
    void decisionCannotBeMadeBeforeAssignmentAndReview() {
        Claim claim = submittedClaim();
        assertThrows(IllegalStateException.class,
                () -> claim.decide("SETTLED", new BigDecimal("100.00")));
    }
}
