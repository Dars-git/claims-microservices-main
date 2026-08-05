package io.claimflow.notifications.application;

import io.claimflow.notifications.model.NotificationEntry;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

/** Thread-safe, in-memory notification read model. */
@Component
public class NotificationLedger {

    private final List<NotificationEntry> entries = new CopyOnWriteArrayList<>();

    public void append(NotificationEntry entry) {
        entries.add(entry);
    }

    public List<NotificationEntry> snapshot() {
        return List.copyOf(entries);
    }

    public int count() {
        return entries.size();
    }
}
