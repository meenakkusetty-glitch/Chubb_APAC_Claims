package com.chubb.claims;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Single entry point for all claims modules in this modular monolith.
 */
@SpringBootApplication
public class ClaimsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClaimsApplication.class, args);
    }
}
