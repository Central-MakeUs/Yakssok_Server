package server.yakssok.global.infra.rabbitmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.medication")
public record MedicationQueueProperties(
	String queue,
	String exchange,
	String routingKey
) {}