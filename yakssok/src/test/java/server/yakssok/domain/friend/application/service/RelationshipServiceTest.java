package server.yakssok.domain.friend.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.application.exception.FriendException;
import server.yakssok.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class RelationshipServiceTest {

	@InjectMocks
	private RelationshipService relationshipService;

	@Mock
	private FriendRepository friendRepository;

	@Nested
	@DisplayName("validateFriendship")
	class ValidateFriendship {

		@Test
		@DisplayName("성공: 이미 친구 관계이면 예외 없이 통과")
		void success_whenAlreadyFriend() {
			Long userId = 1L, friendId = 2L;
			when(friendRepository.isAlreadyFollow(eq(userId), eq(friendId))).thenReturn(true);

			assertDoesNotThrow(() -> relationshipService.validateFriendship(userId, friendId));

			verify(friendRepository).isAlreadyFollow(eq(userId), eq(friendId));
			verifyNoMoreInteractions(friendRepository);
		}

		@Test
		@DisplayName("실패: 친구가 아니면 NOT_FRIEND 예외")
		void fail_whenNotFriend() {
			Long userId = 1L, friendId = 3L;
			when(friendRepository.isAlreadyFollow(eq(userId), eq(friendId))).thenReturn(false);

			FriendException ex = assertThrows(FriendException.class,
				() -> relationshipService.validateFriendship(userId, friendId));

			assertEquals(ErrorCode.NOT_FRIEND, ex.getResponseCode());
			verify(friendRepository).isAlreadyFollow(eq(userId), eq(friendId));
			verifyNoMoreInteractions(friendRepository);
		}
	}

	@Nested
	@DisplayName("validateCanFollow")
	class ValidateCanFollow {

		@Test
		@DisplayName("실패: 자기 자신 팔로우 시도 시 CANNOT_FOLLOW_SELF")
		void fail_selfFollow() {
			Long userId = 1L, followingId = 1L; // 동일 → self follow

			FriendException ex = assertThrows(FriendException.class,
				() -> relationshipService.validateCanFollow(userId, followingId));

			assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, ex.getResponseCode());
			verifyNoInteractions(friendRepository);
		}

		@Test
		@DisplayName("실패: 이미 팔로우한 관계면 ALREADY_FRIEND")
		void fail_alreadyFollow() {
			Long userId = 1L, followingId = 2L;
			when(friendRepository.isAlreadyFollow(eq(userId), eq(followingId))).thenReturn(true);

			FriendException ex = assertThrows(FriendException.class,
				() -> relationshipService.validateCanFollow(userId, followingId));

			assertEquals(ErrorCode.ALREADY_FRIEND, ex.getResponseCode());
			verify(friendRepository).isAlreadyFollow(eq(userId), eq(followingId));
			verifyNoMoreInteractions(friendRepository);
		}

		@Test
		@DisplayName("성공: 자기 자신이 아니고 아직 팔로우가 아니면 통과")
		void success_ok() {
			Long userId = 1L, followingId = 2L;
			when(friendRepository.isAlreadyFollow(eq(userId), eq(followingId))).thenReturn(false);

			assertDoesNotThrow(() -> relationshipService.validateCanFollow(userId, followingId));

			verify(friendRepository).isAlreadyFollow(eq(userId), eq(followingId));
			verifyNoMoreInteractions(friendRepository);
		}

		@Test
		@DisplayName("실패(엣지): 둘 다 null이면 Objects.equals로 같다고 판단되어 CANNOT_FOLLOW_SELF")
		void fail_nullEqualsSelf() {
			Long userId = null, followingId = null;

			FriendException ex = assertThrows(FriendException.class,
				() -> relationshipService.validateCanFollow(userId, followingId));

			assertEquals(ErrorCode.CANNOT_FOLLOW_SELF, ex.getResponseCode());
			verifyNoInteractions(friendRepository);
		}
	}
}
