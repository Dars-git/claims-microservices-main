package io.claimflow.intake.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.claimflow.intake.persistence.ClaimCatalog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClaimReferenceFactoryTest {

    @Mock
    private ClaimCatalog catalog;

    @InjectMocks
    private ClaimReferenceFactory factory;

    @Test
    void createsUppercaseReferenceWithExpectedPrefix() {
        when(catalog.existsByClaimNumber(matches("CLM-[0-9A-F]{8}"))).thenReturn(false);

        String reference = factory.create();

        assertThat(reference).matches("CLM-[0-9A-F]{8}");
        verify(catalog).existsByClaimNumber(reference);
    }
}
