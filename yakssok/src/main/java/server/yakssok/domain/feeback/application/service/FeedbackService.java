package server.yakssok.domain.feeback.application.service;


import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.feeback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feeback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.request.NotificationRequest;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final FriendRepository friendRepository;
	private final UserService userService;
	private final PushService pushService;

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

		NotificationRequest notificationRequest = receiverFollowSender
			.map(friend -> createMutualFeedbackNotificationRequest(sender, receiver, feedback, friend))
			.orElseGet(() -> createOneWayFeedbackNotificationRequest(sender, receiver, feedback));
		pushService.sendNotification(notificationRequest);
	}

	private static NotificationRequest createOneWayFeedbackNotificationRequest(User sender, User receiver, Feedback feedback) {
		return NotificationRequest.fromOneWayFollowFeedback(
			sender.getId(),
			sender.getNickName(),
			receiver.getId(),
			feedback
		);
	}

	private static NotificationRequest createMutualFeedbackNotificationRequest(User sender, User receiver, Feedback feedback,
		Friend friend) {
		return NotificationRequest.fromMutualFollowFeedback(
			sender.getId(),
			receiver.getId(),
			receiver.getNickName(),
			friend.getRelationName(),
			feedback
		);
	}
}
