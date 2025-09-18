package server.yakssok.global.infra.rabbitmq.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rabbitmq.notice")
public record NoticeQueueProperties(
	String queue,
	String exchange,
	String routingKey
)
{ }