package io.claimflow.contracts.events;

/** Shared Kafka destinations used by event producers and consumers. */
public final class ClaimEventTopics {

    public static final String FILED = "claim.submitted";
    public static final String ACCEPTED = "claim.approved";
    public static final String DECLINED = "claim.rejected";

    private ClaimEventTopics() {
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
