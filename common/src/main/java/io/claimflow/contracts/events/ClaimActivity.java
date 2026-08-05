package io.claimflow.contracts.events;

import java.time.Instant;

/** Shared metadata carried by every claim lifecycle event. */
public interface ClaimActivity {

    Long claimId();

    String claimNumber();

    Instant occurredAt();
}
