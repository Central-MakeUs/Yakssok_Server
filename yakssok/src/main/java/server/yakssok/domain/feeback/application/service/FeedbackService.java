package server.yakssok.domain.feeback.application.service;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.feeback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feeback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.applcation.service.FriendService;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.global.infra.rabbitmq.FeedbackQueueProperties;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final FriendService friendService;
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
		Friend friend = friendService.findFriend(sender.getId(), receiver.getId());
		NotificationRequest notificationRequest = NotificationRequest.fromFeedback(
			sender.getId(),
			sender.getNickName(),
			receiver.getId(),
			friend.getRelationName(),
			feedback
		);
		String feedbackExchange = feedbackQueueProperties.exchange();
		String feedbackRoutingKey = feedbackQueueProperties.routingKey();
		rabbitTemplate.convertAndSend(feedbackExchange, feedbackRoutingKey, notificationRequest);
	}
}
