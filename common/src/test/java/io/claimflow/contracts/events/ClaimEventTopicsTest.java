package io.claimflow.contracts.events;

import static org.assertj.core.api.Assertions.assertThat;

import io.claimflow.contracts.model.CoverageCategory;
import java.math.BigDecimal;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class ClaimEventTopicsTest {

    @Test
    void routesEveryLifecycleEventToItsStableTopic() {
        Instant occurredAt = Instant.parse("2026-01-01T00:00:00Z");

        assertThat(ClaimEventTopics.destinationFor(
                new ClaimFiled(1L, "CLM-1", "Jane", CoverageCategory.AUTO,
                        BigDecimal.TEN, occurredAt)))
                .isEqualTo("claim.submitted");
        assertThat(ClaimEventTopics.destinationFor(new ClaimAccepted(1L, "CLM-1", occurredAt)))
                .isEqualTo("claim.approved");
        assertThat(ClaimEventTopics.destinationFor(
                new ClaimDeclined(1L, "CLM-1", "reason", occurredAt)))
                .isEqualTo("claim.rejected");
    }
}
