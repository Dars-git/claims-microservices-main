package io.claimflow.notifications.application;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;

import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.notifications.model.NotificationKind;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NotificationProjectorTest {

    @Mock
    private NotificationLedger ledger;

    @InjectMocks
    private NotificationProjector projector;

    @Test
    void rejectedClaimIncludesReasonInNotification() {
        projector.project(new ClaimDeclined(
                7L, "CLM-7", "Missing receipts", Instant.parse("2026-01-01T00:00:00Z")));

        verify(ledger).append(argThat(entry ->
                entry.type() == NotificationKind.CLAIM_REJECTED
                        && entry.claimNumber().equals("CLM-7")
                        && entry.message().equals("Claim CLM-7 was rejected: Missing receipts")));
    }
}
