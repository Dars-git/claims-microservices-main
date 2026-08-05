package io.claimflow.notifications.messaging;

import static org.assertj.core.api.Assertions.assertThat;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.contracts.model.CoverageCategory;
import io.claimflow.notifications.application.NotificationLedger;
import io.claimflow.notifications.model.NotificationEntry;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

@SpringBootTest
class LocalClaimActivityConsumerTest {

    @Autowired
    private ApplicationEventPublisher eventBus;

    @Autowired
    private NotificationLedger ledger;

    @Test
    void everyLifecycleEventCreatesOneNotification() {
        eventBus.publishEvent(new ClaimFiled(
                1L, "CLM-1", "Jane Doe", CoverageCategory.AUTO,
                new BigDecimal("100"), Instant.now()));
        eventBus.publishEvent(new ClaimAccepted(1L, "CLM-1", Instant.now()));
        eventBus.publishEvent(new ClaimDeclined(2L, "CLM-2", "missing docs", Instant.now()));

        assertThat(ledger.snapshot()).extracting(NotificationEntry::type)
                .containsExactly("CLAIM_SUBMITTED", "CLAIM_APPROVED", "CLAIM_REJECTED");
    }
}
