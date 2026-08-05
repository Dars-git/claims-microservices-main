package io.claimflow.notifications.application;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.notifications.model.NotificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/** Converts transport-neutral claim events into notification entries. */
@Component
public class NotificationProjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationProjector.class);

    private final NotificationLedger ledger;

    public NotificationProjector(NotificationLedger ledger) {
        this.ledger = ledger;
    }

    public void project(ClaimFiled activity) {
        append("CLAIM_SUBMITTED", activity.claimNumber(),
                "Claim " + activity.claimNumber() + " submitted by " + activity.claimantName());
    }

    public void project(ClaimAccepted activity) {
        append("CLAIM_APPROVED", activity.claimNumber(),
                "Claim " + activity.claimNumber() + " was approved");
    }

    public void project(ClaimDeclined activity) {
        append("CLAIM_REJECTED", activity.claimNumber(),
                "Claim " + activity.claimNumber() + " was rejected: " + activity.reason());
    }

    private void append(String kind, String reference, String text) {
        LOGGER.info("Notification [{}] {}", kind, text);
        ledger.append(NotificationEntry.capture(kind, reference, text));
    }
}
