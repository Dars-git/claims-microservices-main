package io.claimflow.intake.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.claimflow.contracts.model.CoverageCategory;
import io.claimflow.intake.application.ClaimWorkflow;
import io.claimflow.intake.errors.ClaimTransitionException;
import io.claimflow.intake.errors.UnknownClaimException;
import io.claimflow.intake.model.ClaimRecord;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ClaimsRestApi.class)
@Import(RestErrorAdvice.class)
class ClaimsRestApiTest {

    @Autowired
    private MockMvc client;

    @MockBean
    private ClaimWorkflow workflow;

    @Test
    void validClaimReturnsCreatedResponse() throws Exception {
        when(workflow.file(any())).thenReturn(sampleClaim());
        String payload = "{\"policyNumber\":\"POL-9\",\"claimantName\":\"Jane Doe\","
                + "\"type\":\"AUTO\",\"description\":\"Collision\",\"amount\":1500.00}";

        client.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/claims/null"))
                .andExpect(jsonPath("$.claimantName").value("Jane Doe"))
                .andExpect(jsonPath("$.status").value("SUBMITTED"));
    }

    @Test
    void invalidClaimReturnsStructuredBadRequest() throws Exception {
        String payload = "{\"policyNumber\":\"\",\"type\":\"AUTO\",\"amount\":100}";

        client.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "claimantName: claimant name is required; "
                                + "policyNumber: policy number is required"))
                .andExpect(jsonPath("$.path").value("/api/claims"));
    }

    @Test
    void unsupportedClaimTypeReturnsStructuredBadRequest() throws Exception {
        String payload = "{\"policyNumber\":\"POL-9\",\"claimantName\":\"Jane Doe\","
                + "\"type\":\"BOAT\",\"amount\":100}";

        client.perform(post("/api/claims").contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(
                        "Request body is malformed or contains an unsupported value"))
                .andExpect(jsonPath("$.path").value("/api/claims"));
    }

    @Test
    void missingClaimReturnsNotFound() throws Exception {
        when(workflow.find(eq(9L))).thenThrow(new UnknownClaimException(9L));

        client.perform(get("/api/claims/9"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void invalidStatusFilterReturnsStructuredBadRequest() throws Exception {
        client.perform(get("/api/claims").queryParam("status", "UNKNOWN"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("status: invalid value 'UNKNOWN'"))
                .andExpect(jsonPath("$.path").value("/api/claims"));
    }

    @Test
    void invalidLifecycleChangeReturnsConflict() throws Exception {
        when(workflow.accept(eq(1L))).thenThrow(new ClaimTransitionException("already approved"));

        client.perform(post("/api/claims/1/approve"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    private ClaimRecord sampleClaim() {
        return new ClaimRecord("CLM-ABC123", "POL-9", "Jane Doe", CoverageCategory.AUTO,
                "Collision", new BigDecimal("1500.00"));
    }
}
