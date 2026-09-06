package io.claimflow.intake.messaging;

import io.claimflow.contracts.events.ClaimActivity;
import io.claimflow.contracts.events.ClaimEventTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** Sends lifecycle events to Kafka, keyed by claim number for per-claim ordering. */
@Component
@Profile("kafka")
public class KafkaClaimEventGateway implements ClaimEventGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaClaimEventGateway.class);

    private final KafkaTemplate<String, Object> kafka;

    public KafkaClaimEventGateway(KafkaTemplate<String, Object> kafka) {
        this.kafka = kafka;
    }

    @Override
    public void dispatch(ClaimActivity activity) {
        String destination = ClaimEventTopics.destinationFor(activity);
        LOGGER.info("Dispatching {} to {} for claim {}",
                activity.getClass().getSimpleName(), destination, activity.claimNumber());
        kafka.send(destination, activity.claimNumber(), activity);
    }
}
