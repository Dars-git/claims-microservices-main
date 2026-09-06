package io.claimflow.intake.messaging;

import io.claimflow.contracts.events.ClaimEventTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.config.TopicBuilder;

/** Creates the three lifecycle topics for the Kafka profile. */
@Configuration
@Profile("kafka")
public class ClaimTopicConfiguration {

    @Bean
    NewTopic filedClaimsTopic() {
        return singlePartitionTopic(ClaimEventTopics.FILED);
    }

    @Bean
    NewTopic acceptedClaimsTopic() {
        return singlePartitionTopic(ClaimEventTopics.ACCEPTED);
    }

    @Bean
    NewTopic declinedClaimsTopic() {
        return singlePartitionTopic(ClaimEventTopics.DECLINED);
    }

    private NewTopic singlePartitionTopic(String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }
}
