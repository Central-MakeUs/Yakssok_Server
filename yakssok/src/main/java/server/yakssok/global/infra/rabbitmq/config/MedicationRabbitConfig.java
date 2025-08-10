package server.yakssok.global.infra.rabbitmq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import server.yakssok.global.infra.rabbitmq.properties.MedicationQueueProperties;

@Configuration
@RequiredArgsConstructor
public class MedicationRabbitConfig {

	private final MedicationQueueProperties medicationQueueProperties;

	@Bean
	public Queue medicationQueue() {
		return QueueBuilder.durable(medicationQueueProperties.queue()).build();
	}
	@Bean
	public DirectExchange medicationExchange() {
		return new DirectExchange(medicationQueueProperties.exchange());
	}
	@Bean
	public Binding medicationBind() {
		return BindingBuilder
			.bind(medicationQueue())
			.to(medicationExchange())
			.with(medicationQueueProperties.routingKey());
	}
}
