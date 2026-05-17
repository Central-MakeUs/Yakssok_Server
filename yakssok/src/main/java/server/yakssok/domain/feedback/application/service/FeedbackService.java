package server.yakssok.domain.feedback.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.feedback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feedback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationDTO;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final UserService userService;
	private final PushService pushService;

	@Transactional
	public void sendFeedback(Long userId, CreateFeedbackRequest request) {
		User sender = userService.getActiveUser(userId);
		User receiver = userService.getActiveUser(request.receiverId());

		Feedback feedback = request.toFeedback(sender, receiver);
		feedbackRepository.save(feedback);
		NotificationDTO notificationDTO = createFeedbackNotificationDto(sender, receiver, feedback);
		pushService.sendNotification(notificationDTO);
	}

	private static NotificationDTO createFeedbackNotificationDto(User sender, User receiver, Feedback feedback) {
		return NotificationDTO.fromFeedback(
			sender.getId(),
			sender.getNickName(),
			receiver.getId(),
			feedback
		);
	}
}
