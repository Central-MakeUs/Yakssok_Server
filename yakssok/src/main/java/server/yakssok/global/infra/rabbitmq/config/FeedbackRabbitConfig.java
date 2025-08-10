package server.yakssok.global.infra.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import server.yakssok.global.infra.rabbitmq.properties.FeedbackQueueProperties;

@Configuration
@RequiredArgsConstructor
public class FeedbackRabbitConfig {

	private final FeedbackQueueProperties feedbackQueueProperties;

	@Bean
	public Queue feedbackQueue() {
		return QueueBuilder.durable(feedbackQueueProperties.queue()).build();
	}
	@Bean
	public DirectExchange feedbackExchange() {
		return new DirectExchange(feedbackQueueProperties.exchange());
	}
	@Bean
	public Binding feedbackBind() {
		return BindingBuilder
			.bind(feedbackQueue())
			.to(feedbackExchange())
			.with(feedbackQueueProperties.routingKey());
	}
}
