package com.chubb.claims.workload;

import com.chubb.claims.events.ClaimEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WorkloadConsumer {
    private final WorkloadRepository repo;
    private final ObjectMapper mapper;

    public WorkloadConsumer(WorkloadRepository r, ObjectMapper m) {
        repo = r;
        mapper = m;
    }

    @KafkaListener(topics = "claim-events", groupId = "workload-service")
    @Transactional
    public void consume(String json) {
        try {
            ClaimEvent e = mapper.readValue(json, ClaimEvent.class);
            if (!java.util.Set.of("CLAIM_SUBMITTED", "CLAIM_ASSIGNED", "INFORMATION_REQUESTED", "INFORMATION_RECEIVED", "CLAIM_DECIDED").contains(e.type()))
                return;
            WorkloadRecord r = repo.findById(e.claimId()).orElseGet(() -> new WorkloadRecord(e.claimId(), e.assignedStaff(), e.status()));
            r.update(e.assignedStaff(), e.status());
            repo.save(r);
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to update workload projection", ex);
        }
    }
}
