package io.claimflow.contracts.events;

import java.time.Instant;

/** Event emitted after a submitted claim has been approved. */
public record ClaimAccepted(
        Long claimId,
        String claimNumber,
        Instant occurredAt) implements ClaimActivity {
}
