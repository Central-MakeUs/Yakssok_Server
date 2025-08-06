package server.yakssok.global.infra.rabbitmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "rabbitmq.feedback")
public record FeedbackQueueProperties(
	String queue,
	String exchange,
	String routingKey)
{ }