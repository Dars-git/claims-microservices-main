package io.claimflow.contracts.events;

import java.time.Instant;

/** Event emitted after a submitted claim has been rejected. */
public record ClaimDeclined(
        Long claimId,
        String claimNumber,
        String reason,
        Instant occurredAt) implements ClaimActivity {
}
