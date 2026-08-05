package io.claimflow.intake.messaging;

import io.claimflow.contracts.events.ClaimActivity;

/** Delivery boundary for claim lifecycle events. */
public interface ClaimEventGateway {

    void dispatch(ClaimActivity activity);
}
