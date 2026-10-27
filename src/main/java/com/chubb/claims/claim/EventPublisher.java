package com.chubb.claims.claim;
import com.chubb.claims.events.ClaimEvent; import com.fasterxml.jackson.core.JsonProcessingException; import com.fasterxml.jackson.databind.ObjectMapper; import org.springframework.kafka.core.KafkaTemplate; import org.springframework.stereotype.Component; import java.time.Instant; import java.util.UUID;
@Component public class EventPublisher { private final KafkaTemplate<String,String> kafka; private final ObjectMapper mapper; public EventPublisher(KafkaTemplate<String,String> kafka,ObjectMapper mapper){this.kafka=kafka;this.mapper=mapper;}
 public void publish(Claim c,String type){try{var e=new ClaimEvent(UUID.randomUUID().toString(),type,c.getId(),c.getStatus().name(),c.getEstimatedLiability(),c.getAssignedStaff(),c.getDecision(),c.getSettlementAmount(),Instant.now());kafka.send("claim-events",c.getId(),mapper.writeValueAsString(e));}catch(JsonProcessingException ex){throw new IllegalStateException("Unable to serialize claim event",ex);}}
}
