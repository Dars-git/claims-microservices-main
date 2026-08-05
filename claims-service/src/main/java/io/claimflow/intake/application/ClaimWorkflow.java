package io.claimflow.intake.application;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.contracts.model.ProcessingState;
import io.claimflow.intake.api.FileClaimCommand;
import io.claimflow.intake.errors.ClaimTransitionException;
import io.claimflow.intake.errors.UnknownClaimException;
import io.claimflow.intake.messaging.ClaimEventGateway;
import io.claimflow.intake.model.ClaimRecord;
import io.claimflow.intake.persistence.ClaimCatalog;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Coordinates claim persistence, lifecycle rules, and event delivery. */
@Service
public class ClaimWorkflow {

    private final ClaimCatalog catalog;
    private final ClaimEventGateway eventGateway;

    public ClaimWorkflow(ClaimCatalog catalog, ClaimEventGateway eventGateway) {
        this.catalog = catalog;
        this.eventGateway = eventGateway;
    }

    @Transactional
    public ClaimRecord file(FileClaimCommand command) {
        ClaimRecord draft = new ClaimRecord(
                nextReference(),
                command.policyNumber(),
                command.claimantName(),
                command.type(),
                command.description(),
                command.amount());
        ClaimRecord persisted = catalog.save(draft);
        eventGateway.dispatch(new ClaimFiled(
                persisted.id(), persisted.reference(), persisted.claimant(),
                persisted.category(), persisted.requestedAmount(), Instant.now()));
        return persisted;
    }

    @Transactional(readOnly = true)
    public ClaimRecord find(Long identifier) {
        return catalog.findById(identifier)
                .orElseThrow(() -> new UnknownClaimException(identifier));
    }

    @Transactional(readOnly = true)
    public List<ClaimRecord> browse(ProcessingState filter) {
        return filter == null
                ? catalog.findAllByOrderByCreatedAtDesc()
                : catalog.findByStatusOrderByCreatedAtDesc(filter);
    }

    @Transactional
    public ClaimRecord accept(Long identifier) {
        ClaimRecord current = find(identifier);
        ensurePending(current, "approve");
        current.accept();
        ClaimRecord persisted = catalog.save(current);
        eventGateway.dispatch(new ClaimAccepted(
                persisted.id(), persisted.reference(), Instant.now()));
        return persisted;
    }

    @Transactional
    public ClaimRecord decline(Long identifier, String explanation) {
        ClaimRecord current = find(identifier);
        ensurePending(current, "reject");
        current.decline(explanation);
        ClaimRecord persisted = catalog.save(current);
        eventGateway.dispatch(new ClaimDeclined(
                persisted.id(), persisted.reference(), explanation, Instant.now()));
        return persisted;
    }

    private void ensurePending(ClaimRecord current, String operation) {
        if (current.state() != ProcessingState.SUBMITTED) {
            throw new ClaimTransitionException(
                    "Cannot " + operation + " claim " + current.reference()
                            + " in status " + current.state());
        }
    }

    private String nextReference() {
        String reference;
        do {
            reference = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (catalog.existsByClaimNumber(reference));
        return reference;
    }
}
