package io.claimflow.contracts.events;

import io.claimflow.contracts.model.CoverageCategory;
import java.math.BigDecimal;
import java.time.Instant;

/** Event emitted after a new claim has been stored. */
public record ClaimFiled(
        Long claimId,
        String claimNumber,
        String claimantName,
        CoverageCategory type,
        BigDecimal amount,
        Instant occurredAt) implements ClaimActivity {
}
