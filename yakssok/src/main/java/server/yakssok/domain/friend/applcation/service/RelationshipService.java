package server.yakssok.domain.friend.applcation.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.friend.applcation.exception.FriendException;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class RelationshipService {
	private final FriendRepository friendRepository;

	public void validateFriendship(Long userId, Long friendId) {
		if (!friendRepository.isAlreadyFollow(userId, friendId)) {
			throw new FriendException(ErrorCode.NOT_FRIEND);
		}
	}

	public void validateCanFollow(Long userId, Long followingId) {
		validateSelfFollow(userId, followingId);
		validateAlreadyFollow(userId, followingId);
	}

	private void validateSelfFollow(Long userId, Long followingId) {
		if (Objects.equals(userId, followingId)) {
			throw new FriendException(ErrorCode.CANNOT_FOLLOW_SELF);
		}
	}

	private void validateAlreadyFollow(Long userId, Long followingId) {
		if (friendRepository.isAlreadyFollow(userId, followingId)) {
			throw new FriendException(ErrorCode.ALREADY_FRIEND);
		}
	}
}