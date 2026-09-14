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
        append(NotificationEntry.submitted(activity.claimNumber(), activity.claimantName()));
    }

    public void project(ClaimAccepted activity) {
        append(NotificationEntry.approved(activity.claimNumber()));
    }

    public void project(ClaimDeclined activity) {
        append(NotificationEntry.rejected(activity.claimNumber(), activity.reason()));
    }

    private void append(NotificationEntry entry) {
        LOGGER.info("Notification [{}] {}", entry.type(), entry.message());
        ledger.append(entry);
    }
}
