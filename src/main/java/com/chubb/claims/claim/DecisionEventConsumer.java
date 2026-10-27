package com.chubb.claims.claim;

import com.chubb.claims.events.ClaimEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DecisionEventConsumer {
    private final ClaimRepository repo;
    private final ObjectMapper mapper;
    private final EventPublisher publisher;

    public DecisionEventConsumer(ClaimRepository r, ObjectMapper m, EventPublisher p) {
        repo = r;
        mapper = m;
        publisher = p;
    }

    @KafkaListener(topics = "claim-events", groupId = "claim-service")
    @Transactional
    public void onEvent(String json) {
        try {
            ClaimEvent e = mapper.readValue(json, ClaimEvent.class);
            if (!"DECISION_RECORDED".equals(e.type())) return;
            Claim c = repo.findById(e.claimId()).orElse(null);
            if (c == null) return;
            c.decide(e.decision(), e.settlementAmount());
            repo.save(c);
            publisher.publish(c, "CLAIM_DECIDED");
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to apply decision event", ex);
        }
    }
}
