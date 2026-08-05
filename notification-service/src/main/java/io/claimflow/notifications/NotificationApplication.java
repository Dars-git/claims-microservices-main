package io.claimflow.notifications;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/** Boots the claim notification projection service. */
@SpringBootApplication
public class NotificationApplication {

    public static void main(String[] arguments) {
        SpringApplication.run(NotificationApplication.class, arguments);
    }
}
