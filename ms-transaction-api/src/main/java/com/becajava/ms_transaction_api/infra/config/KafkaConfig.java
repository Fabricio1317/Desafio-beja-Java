package com.becajava.ms_transaction_api.infra.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.internals.Topic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

public class KafkaConfig {
    @Bean
    public NewTopic topicTransacao(){
        return TopicBuilder.name("transaction.requested").partitions(1).replicas(1).build();
    }

}
