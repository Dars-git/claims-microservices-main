package io.claimflow.intake.graphql;

import io.claimflow.intake.api.ClaimView;
import io.claimflow.intake.application.ClaimWorkflow;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/** GraphQL read adapter backed by the same workflow as the REST API. */
@Controller
public class ClaimsQueryApi {

    private final ClaimWorkflow workflow;

    public ClaimsQueryApi(ClaimWorkflow workflow) {
        this.workflow = workflow;
    }

    @QueryMapping
    public ClaimView claim(@Argument Long id) {
        return ClaimView.render(workflow.find(id));
    }

    @QueryMapping
    public List<ClaimView> claims() {
        return workflow.browse(null).stream().map(ClaimView::render).toList();
    }
}
