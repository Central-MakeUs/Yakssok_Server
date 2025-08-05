package server.yakssok.global.infra.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.medication")
public record MedicationQueueProperties(
	String exchange,
	String routingKey
) {}