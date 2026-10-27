package com.chubb.claims.reporting;

import com.chubb.claims.events.ClaimEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReportingConsumer {
    private final ClaimProjectionRepository repo;
    private final ObjectMapper mapper;

    public ReportingConsumer(ClaimProjectionRepository r, ObjectMapper m) {
        repo = r;
        mapper = m;
    }

    @KafkaListener(topics = "claim-events", groupId = "reporting-service")
    @Transactional
    public void consume(String json) {
        try {
            ClaimEvent e = mapper.readValue(json, ClaimEvent.class);
            if (e.status() == null || e.estimatedLiability() == null) return;
            ClaimProjection p = repo.findById(e.claimId()).orElseGet(() -> new ClaimProjection(e.claimId(), e.status(), e.estimatedLiability()));
            p.update(e.status(), e.estimatedLiability());
            repo.save(p);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to update reporting projection", ex);
        }
    }
}
