package io.claimflow.intake.api;

import io.claimflow.contracts.model.CoverageCategory;
import io.claimflow.contracts.model.ProcessingState;
import io.claimflow.intake.model.ClaimRecord;
import java.math.BigDecimal;
import java.time.Instant;

/** Public representation shared by REST and GraphQL. */
public record ClaimView(
        Long id,
        String claimNumber,
        String policyNumber,
        String claimantName,
        CoverageCategory type,
        String description,
        BigDecimal amount,
        ProcessingState status,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt) {

    public static ClaimView render(ClaimRecord source) {
        return new ClaimView(
                source.id(),
                source.reference(),
                source.policy(),
                source.claimant(),
                source.category(),
                source.details(),
                source.requestedAmount(),
                source.state(),
                source.declineReason(),
                source.createdTime(),
                source.modifiedTime());
    }
}
