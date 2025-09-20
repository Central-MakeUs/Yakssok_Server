package server.yakssok.domain.friend.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import server.yakssok.domain.friend.application.exception.FriendException;
import server.yakssok.domain.friend.domain.entity.Friend;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.friend.presentation.dto.request.FollowFriendRequest;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

	@InjectMocks private FriendService friendService;

	@Mock private UserService userService;
	@Mock private RelationshipService relationshipService;
	@Mock private FriendRepository friendRepository;
	@Mock private FollowFriendRequest followFriendRequest;

	@Nested
	@DisplayName("followFriendByInviteCode")
	class FollowByInviteCode {

		@Nested
		@DisplayName("실패 케이스")
		class FollowByInviteCode_Fail {

			@Test
			@DisplayName("초대코드로 대상 유저 조회 실패시: 예외 전파 & 저장 호출 안 됨")
			void inviteCode_not_found() {
				// given
				Long userId = 100L;
				String inviteCode = "BAD-CODE";

				when(followFriendRequest.inviteCode()).thenReturn(inviteCode);
				when(userService.getUserIdByInviteCode(eq(inviteCode)))
					.thenThrow(new UserException(ErrorCode.INVALID_INVITE_CODE));

				// when & then
				assertThrows(UserException.class,
					() -> friendService.followFriendByInviteCode(userId, followFriendRequest));

				// 다음 단계가 전혀 호출되지 않아야 함
				verify(userService, never()).getActiveUser(anyLong());
				verify(relationshipService, never()).validateCanFollow(anyLong(), anyLong());
				verify(followFriendRequest, never()).toFriend(any(User.class), any(User.class));
				verify(friendRepository, never()).saveAll(anyList());
			}

			@Test
			@DisplayName("관계 검증에서 막힐 때: 예외 전파 & toFriend/saveAll 호출 안 됨")
			void validateCanFollow_fails() {
				// given
				Long userId = 1L;
				String code = "OK";
				Long followingId = 2L;

				User user = mock(User.class);
				User following = mock(User.class);
				when(user.getId()).thenReturn(userId);
				when(following.getId()).thenReturn(followingId);

				when(followFriendRequest.inviteCode()).thenReturn(code);
				when(userService.getUserIdByInviteCode(eq(code))).thenReturn(following);
				when(userService.getActiveUser(eq(userId))).thenReturn(user);

				// 관계검증에서 예외
				doThrow(new FriendException(ErrorCode.ALREADY_FRIEND))
					.when(relationshipService).validateCanFollow(eq(userId), eq(followingId));

				// when & then
				assertThrows(FriendException.class,
					() -> friendService.followFriendByInviteCode(userId, followFriendRequest));

				// 검증에서 막혔으므로 변환/저장은 호출되면 안 됨
				verify(followFriendRequest, never()).toFriend(any(User.class), any(User.class));
				verify(friendRepository, never()).saveAll(anyList());
			}
		}

		@Test
		@DisplayName("초대코드로 팔로우: 관계검증 후 양방향 Friend 두 건을 saveAll로 저장")
		void followFriendByInviteCode_savesBothDirections() {
			// given
			Long userId = 1L;
			String inviteCode = "INV123";

			User user = mock(User.class);
			User following = mock(User.class);
			Long followingId = 2L;

			when(followFriendRequest.inviteCode()).thenReturn(inviteCode);
			when(userService.getUserIdByInviteCode(eq(inviteCode))).thenReturn(following);
			when(userService.getActiveUser(eq(userId))).thenReturn(user);
			when(user.getId()).thenReturn(userId);
			when(following.getId()).thenReturn(followingId);

			// 양방향 Friend 엔티티 Mock
			Friend userToFollowing = mock(Friend.class);
			Friend followingToUser = mock(Friend.class);

			// toFriend 두 번 호출 스텁
			when(followFriendRequest.toFriend(same(user), same(following)))
				.thenReturn(userToFollowing);
			when(followFriendRequest.toFriend(same(following), same(user)))
				.thenReturn(followingToUser);

			// when
			friendService.followFriendByInviteCode(userId, followFriendRequest);

			// then: 호출 순서 검증
			InOrder inOrder = inOrder(userService, relationshipService, followFriendRequest, friendRepository);
			inOrder.verify(userService).getUserIdByInviteCode(eq(inviteCode));
			inOrder.verify(userService).getActiveUser(eq(userId));
			inOrder.verify(relationshipService).validateCanFollow(eq(userId), eq(followingId));
			inOrder.verify(followFriendRequest).toFriend(same(user), same(following));
			inOrder.verify(followFriendRequest).toFriend(same(following), same(user));

			// saveAll에 두 엔티티가 함께 전달되는지 검증
			verify(friendRepository).saveAll(argThat((List<Friend> list) -> {
				assertEquals(2, list.size());
				return list.contains(userToFollowing) && list.contains(followingToUser);
			}));

			verifyNoMoreInteractions(friendRepository);
		}
	}
}
