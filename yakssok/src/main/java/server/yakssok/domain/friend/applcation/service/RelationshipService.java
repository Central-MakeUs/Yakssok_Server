package server.yakssok.domain.friend.applcation.service;

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
		if (!friendRepository.existsByUserIdAndFriendId(userId, friendId)) {
			throw new FriendException(ErrorCode.NOT_FRIEND);
		}
	}
}