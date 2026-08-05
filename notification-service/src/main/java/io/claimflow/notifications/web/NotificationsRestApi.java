package io.claimflow.notifications.web;

import io.claimflow.notifications.application.NotificationLedger;
import io.claimflow.notifications.model.NotificationEntry;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Exposes the notification read model over HTTP. */
@RestController
@RequestMapping("/api/notifications")
public class NotificationsRestApi {

    private final NotificationLedger ledger;

    public NotificationsRestApi(NotificationLedger ledger) {
        this.ledger = ledger;
    }

    @GetMapping
    public List<NotificationEntry> browse() {
        return ledger.snapshot();
    }
}
