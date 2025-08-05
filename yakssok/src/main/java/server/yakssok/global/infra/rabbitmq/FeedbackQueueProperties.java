package server.yakssok.global.infra.rabbitmq;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "rabbitmq.feedback")
public record FeedbackQueueProperties(
	String exchange,
	String routingKey)
{ }