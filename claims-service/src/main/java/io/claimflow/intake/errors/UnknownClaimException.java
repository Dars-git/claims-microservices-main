package io.claimflow.intake.errors;

/** Raised when a client references an id that is not stored. */
public class UnknownClaimException extends RuntimeException {

    public UnknownClaimException(Long identifier) {
        super("Claim not found: " + identifier);
    }
}
