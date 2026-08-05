package io.claimflow.intake.messaging;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimActivity;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;

/** Kafka topic contract and event routing. */
public final class ClaimTopics {

    public static final String FILED = "claim.submitted";
    public static final String ACCEPTED = "claim.approved";
    public static final String DECLINED = "claim.rejected";

    private ClaimTopics() {
    }

    public static String destinationFor(ClaimActivity activity) {
        if (activity instanceof ClaimFiled) {
            return FILED;
        }
        if (activity instanceof ClaimAccepted) {
            return ACCEPTED;
        }
        if (activity instanceof ClaimDeclined) {
            return DECLINED;
        }
        throw new IllegalArgumentException("Unsupported claim activity: " + activity.getClass());
    }
}
