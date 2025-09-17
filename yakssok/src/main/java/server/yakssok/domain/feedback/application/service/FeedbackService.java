package server.yakssok.domain.feedback.application.service;


import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feedback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.infra.rabbitmq.properties.FeedbackQueueProperties;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final FriendRepository friendRepository;
	private final UserService userService;
	private final RabbitTemplate rabbitTemplate;
	private final FeedbackQueueProperties feedbackQueueProperties;


	@Transactional
	public void sendFeedback(Long userId, CreateFeedbackRequest request) {
		User sender = userService.getActiveUser(userId);
		User receiver = userService.getActiveUser(request.receiverId());

		Feedback feedback = request.toFeedback(sender, receiver);
		feedbackRepository.save(feedback);
		pushFeedBackNotification(sender, receiver, feedback);
	}

	private void pushFeedBackNotification(User sender, User receiver, Feedback feedback) {
		Optional<Friend> receiverFollowSender = friendRepository.findByUserIdAndFollowingId(receiver.getId(), sender.getId());

		NotificationDTO notificationDTO = receiverFollowSender
			.map(friend -> createMutualFeedbackNotificationDto(sender, receiver, feedback, friend))
			.orElseGet(() -> createOneWayFeedbackNotificationDto(sender, receiver, feedback));
		pushFeedBackQueue(notificationDTO);
	}

	private void pushFeedBackQueue(NotificationDTO notificationDTO) {
		String feedbackExchange = feedbackQueueProperties.exchange();
		String feedbackRoutingKey = feedbackQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(feedbackExchange, feedbackRoutingKey, notificationDTO);
	}

	private static NotificationDTO createOneWayFeedbackNotificationDto(User sender, User receiver, Feedback feedback) {
		return NotificationDTO.fromOneWayFollowFeedback(
			sender.getId(),
			sender.getNickName(),
			receiver.getId(),
			feedback
		);
	}

	private static NotificationDTO createMutualFeedbackNotificationDto(User sender, User receiver, Feedback feedback,
		Friend friend) {
		return NotificationDTO.fromMutualFollowFeedback(
			sender.getId(),
			receiver.getId(),
			receiver.getNickName(),
			friend.getRelationName(),
			feedback
		);
	}
}
