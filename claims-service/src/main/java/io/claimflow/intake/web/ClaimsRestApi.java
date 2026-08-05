package io.claimflow.intake.web;

import io.claimflow.contracts.model.ProcessingState;
import io.claimflow.intake.api.ClaimView;
import io.claimflow.intake.api.DeclineClaimCommand;
import io.claimflow.intake.api.FileClaimCommand;
import io.claimflow.intake.application.ClaimWorkflow;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** HTTP adapter for claim intake, lookup, and adjudication. */
@RestController
@RequestMapping("/api/claims")
public class ClaimsRestApi {

    private final ClaimWorkflow workflow;

    public ClaimsRestApi(ClaimWorkflow workflow) {
        this.workflow = workflow;
    }

    @PostMapping
    public ResponseEntity<ClaimView> file(@Valid @RequestBody FileClaimCommand command) {
        ClaimView body = ClaimView.render(workflow.file(command));
        return ResponseEntity.created(URI.create("/api/claims/" + body.id())).body(body);
    }

    @GetMapping
    public List<ClaimView> browse(@RequestParam(required = false) ProcessingState status) {
        return workflow.browse(status).stream().map(ClaimView::render).toList();
    }

    @GetMapping("/{id}")
    public ClaimView find(@PathVariable Long id) {
        return ClaimView.render(workflow.find(id));
    }

    @PostMapping("/{id}/approve")
    public ClaimView accept(@PathVariable Long id) {
        return ClaimView.render(workflow.accept(id));
    }

    @PostMapping("/{id}/reject")
    public ClaimView decline(@PathVariable Long id,
                             @Valid @RequestBody DeclineClaimCommand command) {
        return ClaimView.render(workflow.decline(id, command.reason()));
    }
}
