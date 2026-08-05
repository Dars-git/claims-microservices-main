package io.claimflow.intake.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.contracts.model.CoverageCategory;
import io.claimflow.contracts.model.ProcessingState;
import io.claimflow.intake.api.FileClaimCommand;
import io.claimflow.intake.errors.ClaimTransitionException;
import io.claimflow.intake.errors.UnknownClaimException;
import io.claimflow.intake.messaging.ClaimEventGateway;
import io.claimflow.intake.model.ClaimRecord;
import io.claimflow.intake.persistence.ClaimCatalog;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimWorkflowTest {

    @Mock
    private ClaimCatalog catalog;

    @Mock
    private ClaimEventGateway eventGateway;

    @InjectMocks
    private ClaimWorkflow workflow;

    @Test
    void filingPersistsPendingClaimAndDispatchesEvent() {
        when(catalog.existsByClaimNumber(anyString())).thenReturn(false);
        when(catalog.save(any(ClaimRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClaimRecord result = workflow.file(new FileClaimCommand(
                "POL-1", "Jane Doe", CoverageCategory.AUTO, "Collision",
                new BigDecimal("1200.00")));

        assertThat(result.state()).isEqualTo(ProcessingState.SUBMITTED);
        assertThat(result.reference()).startsWith("CLM-");
        verify(eventGateway).dispatch(any(ClaimFiled.class));
    }

    @Test
    void missingIdentifierRaisesNotFoundFailure() {
        when(catalog.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workflow.find(99L))
                .isInstanceOf(UnknownClaimException.class);
    }

    @Test
    void pendingClaimCanBeAccepted() {
        when(catalog.findById(1L)).thenReturn(Optional.of(sampleClaim()));
        when(catalog.save(any(ClaimRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClaimRecord result = workflow.accept(1L);

        assertThat(result.state()).isEqualTo(ProcessingState.APPROVED);
        verify(eventGateway).dispatch(any(ClaimAccepted.class));
    }

    @Test
    void completedClaimCannotTransitionAgain() {
        ClaimRecord completed = sampleClaim();
        completed.accept();
        when(catalog.findById(1L)).thenReturn(Optional.of(completed));

        assertThatThrownBy(() -> workflow.accept(1L))
                .isInstanceOf(ClaimTransitionException.class);
        verify(eventGateway, never()).dispatch(any());
    }

    @Test
    void pendingClaimCanBeDeclinedWithReason() {
        when(catalog.findById(1L)).thenReturn(Optional.of(sampleClaim()));
        when(catalog.save(any(ClaimRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClaimRecord result = workflow.decline(1L, "Missing documents");

        assertThat(result.state()).isEqualTo(ProcessingState.REJECTED);
        assertThat(result.declineReason()).isEqualTo("Missing documents");
        verify(eventGateway).dispatch(any(ClaimDeclined.class));
    }

    private ClaimRecord sampleClaim() {
        return new ClaimRecord("CLM-TEST01", "POL-1", "Jane Doe", CoverageCategory.AUTO,
                "Collision", new BigDecimal("1200.00"));
    }
}
