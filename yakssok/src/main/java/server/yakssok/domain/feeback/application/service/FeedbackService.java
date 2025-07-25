package server.yakssok.domain.feeback.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.feeback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feeback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.applcation.service.RelationshipService;
import server.yakssok.domain.notification.application.service.NotificationService;
import server.yakssok.domain.notification.presentation.dto.NotificationRequest;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final RelationshipService relationshipService;
	private final UserService userService;
	private final NotificationService notificationService;

	@Transactional
	public void sendFeedback(Long userId, CreateFeedbackRequest request) {
		User sender = userService.getUserByUserId(userId);
		User receiver = userService.getUserByUserId(request.receiverId());
		relationshipService.validateFriendship(sender.getId(), receiver.getId());
		Feedback feedback = request.toFeedback(sender, receiver);
		feedbackRepository.save(feedback);

		NotificationRequest notificationRequest = NotificationRequest.fromFeedback(
			sender.getId(),
			sender.getNickName(),
			receiver.getId(),
			feedback
		);
		notificationService.sendNotification(notificationRequest);
	}
}
