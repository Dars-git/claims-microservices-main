package io.claimflow.notifications.application;

import static org.assertj.core.api.Assertions.assertThat;

import io.claimflow.notifications.model.NotificationEntry;
import io.claimflow.notifications.model.NotificationKind;
import org.junit.jupiter.api.Test;

class NotificationLedgerTest {

    @Test
    void snapshotPreservesInsertionOrder() {
        NotificationLedger ledger = new NotificationLedger();
        ledger.append(NotificationEntry.capture(
                NotificationKind.CLAIM_SUBMITTED, "CLM-1", "submitted"));
        ledger.append(NotificationEntry.capture(
                NotificationKind.CLAIM_APPROVED, "CLM-1", "approved"));

        assertThat(ledger.count()).isEqualTo(2);
        assertThat(ledger.snapshot()).extracting(NotificationEntry::claimNumber)
                .containsExactly("CLM-1", "CLM-1");
    }
}
