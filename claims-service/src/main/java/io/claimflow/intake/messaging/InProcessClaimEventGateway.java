package io.claimflow.intake.messaging;

import io.claimflow.contracts.events.ClaimActivity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Delivers events on Spring's application bus when Kafka is not active. */
@Component
@Profile("!kafka")
public class InProcessClaimEventGateway implements ClaimEventGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(InProcessClaimEventGateway.class);

    private final ApplicationEventPublisher eventBus;

    public InProcessClaimEventGateway(ApplicationEventPublisher eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void dispatch(ClaimActivity activity) {
        LOGGER.info("Dispatching {} locally for claim {}",
                activity.getClass().getSimpleName(), activity.claimNumber());
        eventBus.publishEvent(activity);
    }
}
