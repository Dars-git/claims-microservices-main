package io.claimflow.intake.api;

import io.claimflow.contracts.model.CoverageCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Validated body accepted by POST /api/claims. */
public record FileClaimCommand(
        @NotBlank String policyNumber,
        @NotBlank String claimantName,
        @NotNull CoverageCategory type,
        @Size(max = 2000) String description,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal amount) {
}
