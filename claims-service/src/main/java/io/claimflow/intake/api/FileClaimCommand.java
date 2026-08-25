package io.claimflow.intake.api;

import io.claimflow.contracts.model.CoverageCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Validated body accepted by POST /api/claims. */
public record FileClaimCommand(
        @NotBlank(message = "policy number is required") String policyNumber,
        @NotBlank(message = "claimant name is required") String claimantName,
        @NotNull(message = "claim type is required") CoverageCategory type,
        @Size(max = 2000, message = "description must not exceed 2000 characters")
        String description,
        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "amount must be greater than zero")
        BigDecimal amount) {
}
