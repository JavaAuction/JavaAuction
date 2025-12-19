package com.javaauction.alertservice.infrastructure.config;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import tools.jackson.databind.deser.jdk.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, ReqPostInternalAlertsDtoV1> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "alert-group");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                org.apache.kafka.common.serialization.StringDeserializer.class);

        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonDeserializer.class);

        props.put("spring.json.trusted.packages", "*");
        props.put("spring.json.use.type.headers", false);
        props.put("spring.json.value.default.type",
                "com.javaauction.alertservice.presentation.dto.request.ReqPostInternalAlertsDtoV1");

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ReqPostInternalAlertsDtoV1> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ReqPostInternalAlertsDtoV1> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }
}