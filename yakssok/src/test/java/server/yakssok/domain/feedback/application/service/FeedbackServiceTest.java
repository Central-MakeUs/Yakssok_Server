package server.yakssok.domain.feedback.application.service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feedback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.infra.rabbitmq.properties.FeedbackQueueProperties;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

	@InjectMocks
	private FeedbackService feedbackService;

	@Mock private FeedbackRepository feedbackRepository;
	@Mock private UserService userService;
	@Mock private RabbitTemplate rabbitTemplate;
	@Mock private FeedbackQueueProperties feedbackQueueProperties;

	@Mock private CreateFeedbackRequest createFeedbackRequest;

	@Test
	@DisplayName("피드백 전송: Feedback 저장 후 DTO를 만들어 큐에 푸시한다")
	void sendFeedback_pushesNotificationToQueue() {
		// given
		final Long SENDER_ID = 10L;
		final Long RECEIVER_ID = 20L;

		User sender = mock(User.class);
		User receiver = mock(User.class);
		Feedback feedback = mock(Feedback.class);
		NotificationDTO dto = mock(NotificationDTO.class);

		when(sender.getId()).thenReturn(SENDER_ID);
		when(sender.getNickName()).thenReturn("senderNick");
		when(receiver.getId()).thenReturn(RECEIVER_ID);

		when(createFeedbackRequest.receiverId()).thenReturn(RECEIVER_ID);
		when(userService.getActiveUser(SENDER_ID)).thenReturn(sender);
		when(userService.getActiveUser(RECEIVER_ID)).thenReturn(receiver);

		when(createFeedbackRequest.toFeedback(sender, receiver)).thenReturn(feedback);

		when(feedbackQueueProperties.exchange()).thenReturn("ex.feedback");
		when(feedbackQueueProperties.routingKey()).thenReturn("rk.feedback");

		// NotificationDTO 정적 팩토리 모킹
		try (MockedStatic<NotificationDTO> staticMock = mockStatic(NotificationDTO.class)) {
			staticMock.when(() ->
					NotificationDTO.fromFeedback(
						eq(SENDER_ID),
						eq("senderNick"),
						eq(RECEIVER_ID),
						eq(feedback)))
				.thenReturn(dto);

			// when
			feedbackService.sendFeedback(SENDER_ID, createFeedbackRequest);

			// then
			// 유저 조회 및 저장 호출
			verify(userService).getActiveUser(SENDER_ID);
			verify(userService).getActiveUser(RECEIVER_ID);
			verify(feedbackRepository).save(feedback);

			// 정적 메서드 호출 검증
			staticMock.verify(() ->
				NotificationDTO.fromFeedback(SENDER_ID, "senderNick", RECEIVER_ID, feedback));

			// 큐 전송 검증
			verify(feedbackQueueProperties).exchange();
			verify(feedbackQueueProperties).routingKey();
			verify(rabbitTemplate).convertAndSend("ex.feedback", "rk.feedback", dto);

			// 불필요 상호작용 없음
			verifyNoMoreInteractions(rabbitTemplate, feedbackRepository, userService, feedbackQueueProperties);
		}
	}
}
