package server.yakssok.domain.feedback.application.service;

import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.MockedStatic;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feedback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.infra.rabbitmq.properties.FeedbackQueueProperties;

@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

	@InjectMocks
	private FeedbackService feedbackService;

	@Mock private FeedbackRepository feedbackRepository;
	@Mock private FriendRepository friendRepository;
	@Mock private UserService userService;
	@Mock private RabbitTemplate rabbitTemplate;
	@Mock private FeedbackQueueProperties feedbackQueueProperties;

	@Mock private CreateFeedbackRequest createFeedbackRequest;

	@Nested
	@DisplayName("sendFeedback")
	class SendFeedback {

		private final Long SENDER_ID = 10L;
		private final Long RECEIVER_ID = 20L;

		private User mockSender() {
			User u = mock(User.class);
			when(u.getId()).thenReturn(SENDER_ID);
			return u;
		}

		private User mockReceiver() {
			User u = mock(User.class);
			when(u.getId()).thenReturn(RECEIVER_ID);
			return u;
		}

		private void stubCommonQueueProps() {
			when(feedbackQueueProperties.exchange()).thenReturn("ex.feedback");
			when(feedbackQueueProperties.routingKey()).thenReturn("rk.feedback");
		}

		@Test
		@DisplayName("상대가 나를 팔로우하지 않을 때: OneWay 알림을 만들어 큐에 푸시한다")
		void oneWayFollow_pushesOneWayNotification() {
			// given
			User sender = mockSender();
			User receiver = mockReceiver();
			Feedback feedback = mock(Feedback.class);
			NotificationDTO oneWayDto = mock(NotificationDTO.class);

			when(sender.getNickName()).thenReturn("senderNick");
			when(createFeedbackRequest.receiverId()).thenReturn(RECEIVER_ID);
			when(userService.getActiveUser(SENDER_ID)).thenReturn(sender);
			when(userService.getActiveUser(RECEIVER_ID)).thenReturn(receiver);

			when(createFeedbackRequest.toFeedback(sender, receiver)).thenReturn(feedback);
			when(friendRepository.findByUserIdAndFollowingId(RECEIVER_ID, SENDER_ID))
				.thenReturn(Optional.empty());

			stubCommonQueueProps();

			// NotificationDTO 정적 팩토리 모킹
			try (MockedStatic<NotificationDTO> staticMock = mockStatic(NotificationDTO.class)) {
				staticMock.when(() ->
						NotificationDTO.fromOneWayFollowFeedback(
							eq(SENDER_ID),
							eq("senderNick"),
							eq(RECEIVER_ID),
							eq(feedback)))
					.thenReturn(oneWayDto);

				// when
				feedbackService.sendFeedback(SENDER_ID, createFeedbackRequest);

				// then
				verify(userService).getActiveUser(SENDER_ID);
				verify(userService).getActiveUser(RECEIVER_ID);

				verify(feedbackRepository).save(feedback);

				// 정적 메서드가 호출되었는지 검증
				staticMock.verify(() ->
					NotificationDTO.fromOneWayFollowFeedback(
						SENDER_ID, "senderNick", RECEIVER_ID, feedback));

				// 큐 전송 검증
				verify(rabbitTemplate).convertAndSend("ex.feedback", "rk.feedback", oneWayDto);
				verifyNoMoreInteractions(rabbitTemplate);
			}
		}

		@Test
		@DisplayName("상대가 나를 팔로우할 때(맞팔): Mutual 알림을 만들어 큐에 푸시한다")
		void mutualFollow_pushesMutualNotification() {
			// given
			User sender = mockSender();
			User receiver = mockReceiver();
			Feedback feedback = mock(Feedback.class);
			Friend friend = mock(Friend.class);
			NotificationDTO mutualDto = mock(NotificationDTO.class);

			when(receiver.getNickName()).thenReturn("receiverNick");
			when(createFeedbackRequest.receiverId()).thenReturn(RECEIVER_ID);
			when(userService.getActiveUser(SENDER_ID)).thenReturn(sender);
			when(userService.getActiveUser(RECEIVER_ID)).thenReturn(receiver);

			when(createFeedbackRequest.toFeedback(sender, receiver)).thenReturn(feedback);

			when(friendRepository.findByUserIdAndFollowingId(RECEIVER_ID, SENDER_ID))
				.thenReturn(Optional.of(friend));
			when(friend.getRelationName()).thenReturn("bestie");

			stubCommonQueueProps();

			// NotificationDTO 정적 팩토리 모킹
			try (MockedStatic<NotificationDTO> staticMock = mockStatic(NotificationDTO.class)) {
				staticMock.when(() ->
						NotificationDTO.fromMutualFollowFeedback(
							eq(SENDER_ID),
							eq(RECEIVER_ID),
							eq("receiverNick"),
							eq("bestie"),
							eq(feedback)))
					.thenReturn(mutualDto);

				// when
				feedbackService.sendFeedback(SENDER_ID, createFeedbackRequest);

				// then
				verify(userService).getActiveUser(SENDER_ID);
				verify(userService).getActiveUser(RECEIVER_ID);

				verify(feedbackRepository).save(feedback);

				// 정적 메서드 호출 검증
				staticMock.verify(() ->
					NotificationDTO.fromMutualFollowFeedback(
						SENDER_ID, RECEIVER_ID, "receiverNick", "bestie", feedback));

				// 큐 전송 검증
				verify(rabbitTemplate).convertAndSend("ex.feedback", "rk.feedback", mutualDto);
				verifyNoMoreInteractions(rabbitTemplate);
			}
		}
	}
}
