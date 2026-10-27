package com.chubb.claims.assessment;

import com.chubb.claims.claim.Claim;
import com.chubb.claims.claim.ClaimRepository;
import com.chubb.claims.claim.ClaimStatus;
import com.chubb.claims.events.ClaimEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/** Assessment endpoints live in the same application; decisions still travel asynchronously through Kafka. */
@RestController
@RequestMapping("/api/assessments")
public class AssessmentController {
    private static final String TOPIC = "claim-events";

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;
    private final ClaimRepository claims;

    public AssessmentController(KafkaTemplate<String, String> kafka, ObjectMapper mapper, ClaimRepository claims) {
        this.kafka = kafka;
        this.mapper = mapper;
        this.claims = claims;
    }

    public record DecisionRequest(@NotBlank String decision, @DecimalMin("0.0") BigDecimal settlementAmount) { }

    @PostMapping("/{claimId}/decision")
    public ResponseEntity<?> decide(@PathVariable String claimId, @Valid @RequestBody DecisionRequest request) {
        Claim current = claims.findById(claimId).orElse(null);
        if (current == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Claim not found");
        }
        if (current.getStatus() != ClaimStatus.UNDER_REVIEW) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Claim must be UNDER_REVIEW before a decision");
        }

        String decision = request.decision();
        BigDecimal amount = request.settlementAmount();
        if ("SETTLED".equals(decision)) {
            if (amount == null || amount.signum() < 0) {
                return ResponseEntity.badRequest().body("Settlement amount is required and must be non-negative");
            }
        } else if ("REJECTED".equals(decision)) {
            if (amount != null) {
                return ResponseEntity.badRequest().body("Do not supply settlementAmount for rejection");
            }
        } else {
            return ResponseEntity.badRequest().body("decision must be SETTLED or REJECTED");
        }

        try {
            ClaimEvent event = new ClaimEvent(UUID.randomUUID().toString(), "DECISION_RECORDED", claimId,
                    null, null, null, decision, amount, Instant.now());
            kafka.send(TOPIC, claimId, mapper.writeValueAsString(event));
            return ResponseEntity.accepted().body(Map.of(
                    "claimId", claimId,
                    "message", "Decision accepted for asynchronous processing"));
        } catch (JsonProcessingException exception) {
            return ResponseEntity.internalServerError().body("Could not serialize decision event");
        }
    }
}
