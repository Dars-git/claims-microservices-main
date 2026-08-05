package io.claimflow.notifications.messaging;

import io.claimflow.contracts.events.ClaimAccepted;
import io.claimflow.contracts.events.ClaimDeclined;
import io.claimflow.contracts.events.ClaimFiled;
import io.claimflow.notifications.application.NotificationProjector;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/** Receives Spring application events in the default profile. */
@Component
@Profile("!kafka")
public class LocalClaimActivityConsumer {

    private final NotificationProjector projector;

    public LocalClaimActivityConsumer(NotificationProjector projector) {
        this.projector = projector;
    }

    @EventListener
    public void claimFiled(ClaimFiled activity) {
        projector.project(activity);
    }

    @EventListener
    public void claimAccepted(ClaimAccepted activity) {
        projector.project(activity);
    }

    @EventListener
    public void claimDeclined(ClaimDeclined activity) {
        projector.project(activity);
    }
}
