package io.claimflow.intake.application;

import io.claimflow.intake.persistence.ClaimCatalog;
import java.util.UUID;
import org.springframework.stereotype.Component;

/** Creates collision-resistant public claim references. */
@Component
public class ClaimReferenceFactory {

    private static final String PREFIX = "CLM-";

    private final ClaimCatalog catalog;

    public ClaimReferenceFactory(ClaimCatalog catalog) {
        this.catalog = catalog;
    }

    public String create() {
        String candidate;
        do {
            candidate = PREFIX + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (catalog.existsByClaimNumber(candidate));
        return candidate;
    }
}
