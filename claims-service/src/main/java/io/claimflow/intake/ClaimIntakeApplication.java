package io.claimflow.intake;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Boots the claim intake and adjudication API. */
@SpringBootApplication
public class ClaimIntakeApplication {

    public static void main(String[] arguments) {
        SpringApplication.run(ClaimIntakeApplication.class, arguments);
    }
}
