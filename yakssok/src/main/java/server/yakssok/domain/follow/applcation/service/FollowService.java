package server.yakssok.domain.follow.applcation.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.follow.applcation.exception.FollowException;
import server.yakssok.domain.follow.domain.entity.Follow;
import server.yakssok.domain.follow.domain.repository.FollowRepository;
import server.yakssok.domain.follow.presentation.dto.request.FollowRequest;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class FollowService {
	private final UserService userService;
	private final FollowRepository followRepository;

	@Transactional
	public void followByInviteCode(Long userId, FollowRequest followRequest) {
		String inviteCode = followRequest.inviteCode();
		String relationName = followRequest.relationName();

		Long followingId = userService.getUserIdByInviteCode(inviteCode);
		validateCanFollow(userId, followingId);
		createFriend(userId, followingId, relationName);
	}

	private void createFriend(Long userId, Long followingId, String relationName) {
		Follow follow = Follow.create(userId, followingId, relationName);
		followRepository.save(follow);
	}

	private void validateCanFollow(Long userId, Long followingId) {
		validateSelfFollow(userId, followingId);
		validateAlreadyFriend(userId, followingId);
	}

	private static void validateSelfFollow(Long userId, Long followingId) {
		boolean isSelf = Objects.equals(userId, followingId);
		if (isSelf) {
			throw new FollowException(ErrorCode.CANNOT_FOLLOW_SELF);
		}
	}

	private void validateAlreadyFriend(Long userId, Long followingId) {
		boolean isExists = followRepository.existsByFollowerIdAndFollowingId(userId, followingId);
		if (isExists) {
			throw new FollowException(ErrorCode.ALREADY_FRIEND);
		}
	}
}

