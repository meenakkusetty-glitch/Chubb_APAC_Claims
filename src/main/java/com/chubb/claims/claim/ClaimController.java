package com.chubb.claims.claim;
import jakarta.validation.Valid; import jakarta.validation.constraints.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import org.springframework.web.server.ResponseStatusException; import java.math.BigDecimal; import java.util.*;
@RestController @RequestMapping("/api/claims") public class ClaimController {
 private final ClaimRepository repo; private final EventPublisher events; public ClaimController(ClaimRepository repo,EventPublisher events){this.repo=repo;this.events=events;}
 public record CreateClaim(@NotBlank String claimantName,@NotBlank String incidentDescription,@NotNull @DecimalMin("0.0") BigDecimal estimatedLiability){}
 public record AssignRequest(@NotBlank String staffName){}
 @PostMapping public ResponseEntity<Claim> create(@Valid @RequestBody CreateClaim r){Claim c=repo.save(new Claim(UUID.randomUUID().toString(),r.claimantName(),r.incidentDescription(),r.estimatedLiability()));events.publish(c,"CLAIM_SUBMITTED");return ResponseEntity.status(201).body(c);}
 @GetMapping("/{id}") public Claim get(@PathVariable String id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Claim not found"));}
 @GetMapping public List<Claim> list(@RequestParam(required=false) ClaimStatus status){return status==null?repo.findAll():repo.findByStatus(status);}
 @PostMapping("/{id}/assign") public Claim assign(@PathVariable String id,@Valid @RequestBody AssignRequest r){Claim c=get(id);if(c.getStatus()!=ClaimStatus.SUBMITTED&&c.getStatus()!=ClaimStatus.UNDER_REVIEW)throw new ResponseStatusException(HttpStatus.CONFLICT,"Cannot assign claim in current status");c.assign(r.staffName());repo.save(c);events.publish(c,"CLAIM_ASSIGNED");return c;}
 @PostMapping("/{id}/information-requests") public Claim requestInfo(@PathVariable String id){Claim c=get(id);try{c.requestInformation();}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}repo.save(c);events.publish(c,"INFORMATION_REQUESTED");return c;}
 @PostMapping("/{id}/information") public Claim provideInfo(@PathVariable String id){Claim c=get(id);try{c.provideInformation();}catch(IllegalStateException e){throw new ResponseStatusException(HttpStatus.CONFLICT,e.getMessage());}repo.save(c);events.publish(c,"INFORMATION_RECEIVED");return c;}
}
