package com.chubb.claims.claim;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, String> {
    List<Claim> findByStatus(ClaimStatus status);
}
