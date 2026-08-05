package io.claimflow.notifications.messaging;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.notifications.application.NotificationProjector;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/** Receives claim lifecycle events from Kafka. */
@Component
@Profile("kafka")
public class KafkaClaimActivityConsumer {

    private final NotificationProjector projector;

    public KafkaClaimActivityConsumer(NotificationProjector projector) {
        this.projector = projector;
    }

    @KafkaListener(topics = "claim.submitted", groupId = "notification-service")
    public void claimFiled(ClaimFiled activity) {
        projector.project(activity);
    }

    @KafkaListener(topics = "claim.approved", groupId = "notification-service")
    public void claimAccepted(ClaimAccepted activity) {
        projector.project(activity);
    }

    @KafkaListener(topics = "claim.rejected", groupId = "notification-service")
    public void claimDeclined(ClaimDeclined activity) {
        projector.project(activity);
    }
}
