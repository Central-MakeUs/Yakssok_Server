package server.yakssok.global.infra.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitConfig {


	// 피드백 알림
	@Bean
	public Queue feedbackQueue() {
		return QueueBuilder.durable("feedback-queue").build();
	}
	@Bean
	public DirectExchange feedbackExchange() {
		return new DirectExchange("feedback-exchange");
	}
	@Bean
	public Binding feedbackBind() {
		return BindingBuilder
			.bind(feedbackQueue())
			.to(feedbackExchange())
			.with("feedback-key");
	}


	// 미복용(본인) 알림
	@Bean
	public Queue notTakenQueue() {
		return QueueBuilder.durable("not-taken-queue").build();
	}
	@Bean
	public DirectExchange notTakenExchange() {
		return new DirectExchange("not-taken-exchange");
	}
	@Bean
	public Binding notTakenBind() {
		return BindingBuilder
			.bind(notTakenQueue())
			.to(notTakenExchange())
			.with("not-taken-key");
	}

	// 미복용 고발(친구) 알림
	@Bean
	public Queue reportQueue() {
		return QueueBuilder.durable("report-queue").build();
	}
	@Bean
	public DirectExchange reportExchange() {
		return new DirectExchange("report-exchange");
	}
	@Bean
	public Binding reportBind() {
		return BindingBuilder
			.bind(reportQueue())
			.to(reportExchange())
			.with("report-key");
	}


	// 복약 안내(정시) 알림 (medication)
	@Bean
	public Queue medicationQueue() {
		return QueueBuilder.durable("medication-queue").build();
	}
	@Bean
	public DirectExchange medicationExchange() {
		return new DirectExchange("medication-exchange");
	}
	@Bean
	public Binding medicationBind() {
		return BindingBuilder
			.bind(medicationQueue())
			.to(medicationExchange())
			.with("medication-key");
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(
		ConnectionFactory connectionFactory,
		MessageConverter messageConverter
	) {
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setMessageConverter(messageConverter);
		return template;
	}
}
