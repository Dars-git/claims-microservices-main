package io.claimflow.intake.errors;

/** Raised when the requested lifecycle transition is not allowed. */
public class ClaimTransitionException extends RuntimeException {

    public ClaimTransitionException(String detail) {
        super(detail);
    }
}
