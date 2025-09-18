package server.yakssok.global.infra.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import server.yakssok.global.infra.rabbitmq.properties.NoticeQueueProperties;

@Configuration
@RequiredArgsConstructor
public class NoticeRabbitConfig {

	private final NoticeQueueProperties noticeQueueProperties;

	@Bean
	public Queue noticeQueue() {
		return QueueBuilder.durable(noticeQueueProperties.queue())
			.build();
	}

	@Bean
	public DirectExchange noticeExchange() {
		return new DirectExchange(noticeQueueProperties.exchange());
	}

	@Bean
	public Binding noticeBinding() {
		return BindingBuilder
			.bind(noticeQueue())
			.to(noticeExchange())
			.with(noticeQueueProperties.routingKey());
	}

}