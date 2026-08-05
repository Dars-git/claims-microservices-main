package io.claimflow.intake.messaging;

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
        return singlePartitionTopic(ClaimTopics.FILED);
    }

    @Bean
    NewTopic acceptedClaimsTopic() {
        return singlePartitionTopic(ClaimTopics.ACCEPTED);
    }

    @Bean
    NewTopic declinedClaimsTopic() {
        return singlePartitionTopic(ClaimTopics.DECLINED);
    }

    private NewTopic singlePartitionTopic(String name) {
        return TopicBuilder.name(name).partitions(1).replicas(1).build();
    }
}
