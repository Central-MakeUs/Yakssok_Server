package server.yakssok.domain.feeback.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.feeback.domain.entity.FeedbackException;
import server.yakssok.domain.feeback.domain.repository.FeedbackRepository;
import server.yakssok.domain.feeback.presentation.dto.request.CreateFeedbackRequest;
import server.yakssok.domain.friend.applcation.service.FriendService;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FeedbackService {
	private final FeedbackRepository feedbackRepository;
	private final FriendService friendService;
	private final UserService userService;

	@Transactional
	public void sendFeedback(Long userId, CreateFeedbackRequest request) {
		User sender = userService.getUserByUserId(userId);
		User receiver = userService.getUserByUserId(request.receiverId());
		validateIsFriend(sender, receiver);
		Feedback feedback = request.toFeedback(sender, receiver);
		feedbackRepository.save(feedback);
	}

	private void validateIsFriend(User sender, User receiver) {
		if (!friendService.isFollowing(sender.getId(), receiver.getId())) {
			throw new FeedbackException(ErrorCode.NOT_FRIEND);
		}
	}
}
