package io.claimflow.notifications.model;

import java.time.Instant;

/** Read-model entry produced from a claim lifecycle event. */
public record NotificationEntry(
        NotificationKind type,
        String claimNumber,
        String message,
        Instant receivedAt) {

    public static NotificationEntry capture(
            NotificationKind kind, String reference, String text) {
        return new NotificationEntry(kind, reference, text, Instant.now());
    }

    public static NotificationEntry submitted(String reference, String claimant) {
        return capture(NotificationKind.CLAIM_SUBMITTED, reference,
                "Claim " + reference + " submitted by " + claimant);
    }

    public static NotificationEntry approved(String reference) {
        return capture(NotificationKind.CLAIM_APPROVED, reference,
                "Claim " + reference + " was approved");
    }

    public static NotificationEntry rejected(String reference, String reason) {
        return capture(NotificationKind.CLAIM_REJECTED, reference,
                "Claim " + reference + " was rejected: " + reason);
    }
}
