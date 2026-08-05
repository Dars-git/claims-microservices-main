package io.claimflow.notifications.model;

import java.time.Instant;

/** Read-model entry produced from a claim lifecycle event. */
public record NotificationEntry(
        String type,
        String claimNumber,
        String message,
        Instant receivedAt) {

    public static NotificationEntry capture(String kind, String reference, String text) {
        return new NotificationEntry(kind, reference, text, Instant.now());
    }
}
