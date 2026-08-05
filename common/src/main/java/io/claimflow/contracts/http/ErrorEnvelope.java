package io.claimflow.contracts.http;

import java.time.Instant;

/** Stable JSON shape used for REST errors. */
public record ErrorEnvelope(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {

    public static ErrorEnvelope create(int code, String title, String detail, String requestPath) {
        return new ErrorEnvelope(Instant.now(), code, title, detail, requestPath);
    }
}
