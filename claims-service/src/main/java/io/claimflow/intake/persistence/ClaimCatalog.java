package io.claimflow.intake.persistence;

import io.claimflow.contracts.model.ProcessingState;
import io.claimflow.intake.model.ClaimRecord;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Persistence port for claim records. */
public interface ClaimCatalog extends JpaRepository<ClaimRecord, Long> {

    List<ClaimRecord> findByStatusOrderByCreatedAtDesc(ProcessingState state);

    List<ClaimRecord> findAllByOrderByCreatedAtDesc();

    boolean existsByClaimNumber(String reference);
}
