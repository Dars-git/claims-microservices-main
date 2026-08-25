package io.claimflow.intake.api;

import jakarta.validation.constraints.NotBlank;

/** Validated body accepted by the claim rejection endpoint. */
public record DeclineClaimCommand(@NotBlank(message = "rejection reason is required") String reason) {
}
