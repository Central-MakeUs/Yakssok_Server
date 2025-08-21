package server.yakssok.domain.user.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import server.yakssok.domain.auth.application.service.AuthService;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.domain.user.presentation.controller.CompleteMyInfoRequest;
import server.yakssok.domain.user.presentation.dto.request.UpdateUserInfoRequest;
import server.yakssok.domain.user.presentation.dto.response.FindMyInfoResponse;
import server.yakssok.domain.user.presentation.dto.response.FindUserInfoResponse;
import server.yakssok.domain.user.presentation.dto.response.FindUserInviteCodeResponse;
import server.yakssok.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@InjectMocks private UserService userService;

	@Mock private UserRepository userRepository;
	@Mock private UserDeletionService userDeletionService;
	@Mock private MedicationRepository medicationRepository;
	@Mock private FriendRepository friendRepository;
	@Mock private AuthService authService;

	@Nested
	@DisplayName("findMyInfo")
	class FindMyInfo {

		@Test
		@DisplayName("성공: 사용자/복용개수/팔로잉개수로 응답 생성")
		void success() {
			Long userId = 1L;
			User user = mock(User.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));
			when(medicationRepository.countUserTakingMedication(eq(userId)))
				.thenReturn(3);
			when(friendRepository.countByUserId(eq(userId)))
				.thenReturn(5);

			FindMyInfoResponse response = mock(FindMyInfoResponse.class);

			try (MockedStatic<FindMyInfoResponse> staticMock = mockStatic(FindMyInfoResponse.class)) {
				staticMock.when(() ->
					FindMyInfoResponse.of(same(user), eq(3), eq(5))
				).thenReturn(response);

				FindMyInfoResponse result = userService.findMyInfo(userId);

				assertSame(response, result);
				verify(userRepository).findByIdAndIsDeletedFalse(eq(userId));
				verify(medicationRepository).countUserTakingMedication(eq(userId));
				verify(friendRepository).countByUserId(eq(userId));
			}
		}

		@Test
		@DisplayName("실패: 사용자 없음 → NOT_FOUND_USER")
		void userNotFound() {
			Long userId = 2L;
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.findMyInfo(userId));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());

			verify(userRepository).findByIdAndIsDeletedFalse(eq(userId));
			verifyNoInteractions(medicationRepository, friendRepository);
		}
	}

	@Nested
	@DisplayName("updateUserInfo")
	class UpdateUserInfo {

		@Test
		@DisplayName("성공: getActiveUser 후 updateInfo 호출")
		void success() {
			Long userId = 1L;
			User user = mock(User.class);
			UpdateUserInfoRequest req = mock(UpdateUserInfoRequest.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));
			when(req.nickname()).thenReturn("nick");
			when(req.profileImageUrl()).thenReturn("img.png");

			userService.updateUserInfo(userId, req);

			verify(user).updateInfo(eq("nick"), eq("img.png"));
		}

		@Test
		@DisplayName("실패: 사용자 없음 → NOT_FOUND_USER")
		void userNotFound() {
			Long userId = 2L;
			UpdateUserInfoRequest req = mock(UpdateUserInfoRequest.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.updateUserInfo(userId, req));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());
		}
	}

	@Nested
	@DisplayName("getActiveUser")
	class GetActiveUser {

		@Test
		@DisplayName("성공: 사용자 반환")
		void success() {
			Long userId = 1L;
			User user = mock(User.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));

			User result = userService.getActiveUser(userId);
			assertSame(user, result);
		}

		@Test
		@DisplayName("실패: NOT_FOUND_USER")
		void notFound() {
			Long userId = 1L;
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.getActiveUser(userId));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());
		}
	}

	@Nested
	@DisplayName("findUserInviteCode")
	class FindUserInviteCode {

		@Test
		@DisplayName("성공: 유저의 초대코드 value로 응답 생성")
		void success() {
			Long userId = 10L;
			// deep stubs로 user.getInviteCode().getValue()
			User user = mock(User.class, RETURNS_DEEP_STUBS);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));
			when(user.getInviteCode().getValue())
				.thenReturn("INV-123");

			FindUserInviteCodeResponse resp = mock(FindUserInviteCodeResponse.class);
			try (MockedStatic<FindUserInviteCodeResponse> staticMock =
					 mockStatic(FindUserInviteCodeResponse.class)) {

				staticMock.when(() -> FindUserInviteCodeResponse.of(eq("INV-123")))
					.thenReturn(resp);

				FindUserInviteCodeResponse result = userService.findUserInviteCode(userId);
				assertSame(resp, result);

				verify(userRepository).findByIdAndIsDeletedFalse(eq(userId));
				// deep stub 경유 확인(선택)
				verify(user.getInviteCode()).getValue();
			}
		}

		@Test
		@DisplayName("실패: 사용자 없음 → NOT_FOUND_USER")
		void userNotFound() {
			Long userId = 11L;
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.findUserInviteCode(userId));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());
		}
	}

	@Nested
	@DisplayName("findUserInfoByInviteCode")
	class FindUserInfoByInviteCode {

		@Test
		@DisplayName("성공: 코드로 찾은 유저로 응답 생성")
		void success() {
			String code = "INV-999";
			User user = mock(User.class);
			when(userRepository.findByInviteCodeValue(eq(code)))
				.thenReturn(Optional.of(user));

			FindUserInfoResponse resp = mock(FindUserInfoResponse.class);
			try (MockedStatic<FindUserInfoResponse> staticMock =
					 mockStatic(FindUserInfoResponse.class)) {

				staticMock.when(() -> FindUserInfoResponse.from(same(user)))
					.thenReturn(resp);

				FindUserInfoResponse result = userService.findUserInfoByInviteCode(code);
				assertSame(resp, result);

				verify(userRepository).findByInviteCodeValue(eq(code));
			}
		}

		@Test
		@DisplayName("실패: 유효하지 않은 초대코드 → INVALID_INVITE_CODE")
		void invalidCode() {
			String code = "BAD";
			when(userRepository.findByInviteCodeValue(eq(code)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.findUserInfoByInviteCode(code));
			assertEquals(ErrorCode.INVALID_INVITE_CODE, ex.getResponseCode());
		}
	}

	@Nested
	@DisplayName("getUserIdByInviteCode")
	class GetUserIdByInviteCode {

		@Test
		@DisplayName("성공: 코드로 유저 반환")
		void success() {
			String code = "INV-777";
			User user = mock(User.class);
			when(userRepository.findByInviteCodeValue(eq(code)))
				.thenReturn(Optional.of(user));

			User result = userService.getUserIdByInviteCode(code);
			assertSame(user, result);
			verify(userRepository).findByInviteCodeValue(eq(code));
		}

		@Test
		@DisplayName("실패: INVALID_INVITE_CODE")
		void invalidCode() {
			String code = "NONE";
			when(userRepository.findByInviteCodeValue(eq(code)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.getUserIdByInviteCode(code));
			assertEquals(ErrorCode.INVALID_INVITE_CODE, ex.getResponseCode());
		}
	}

	@Nested
	@DisplayName("deleteUser")
	class DeleteUser {

		@Test
		@DisplayName("성공: getActiveUser → unlinkOAuth → deleteUser 순서")
		void success() {
			Long userId = 1L;
			User user = mock(User.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));

			userService.deleteUser(userId);

			InOrder io = inOrder(userRepository, authService, userDeletionService);
			io.verify(userRepository).findByIdAndIsDeletedFalse(eq(userId));
			io.verify(authService).unlinkOAuth(same(user));
			io.verify(userDeletionService).deleteUser(same(user));
		}

		@Test
		@DisplayName("실패: 사용자 없음 → NOT_FOUND_USER, unlink/delete 호출 없음")
		void userNotFound() {
			Long userId = 1L;
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.deleteUser(userId));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());

			verifyNoInteractions(authService, userDeletionService);
		}
	}

	@Nested
	@DisplayName("initializeMyInfo")
	class InitializeMyInfo {

		@Test
		@DisplayName("성공: getActiveUser 후 initializeUserInfo 호출")
		void success() {
			Long userId = 1L;
			User user = mock(User.class);
			CompleteMyInfoRequest req = mock(CompleteMyInfoRequest.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.of(user));
			when(req.nickName()).thenReturn("NEW_NICK");

			userService.initializeMyInfo(userId, req);

			verify(user).initializeUserInfo(eq("NEW_NICK"));
		}

		@Test
		@DisplayName("실패: 사용자 없음 → NOT_FOUND_USER")
		void userNotFound() {
			Long userId = 1L;
			CompleteMyInfoRequest req = mock(CompleteMyInfoRequest.class);
			when(userRepository.findByIdAndIsDeletedFalse(eq(userId)))
				.thenReturn(Optional.empty());

			UserException ex = assertThrows(UserException.class,
				() -> userService.initializeMyInfo(userId, req));
			assertEquals(ErrorCode.NOT_FOUND_USER, ex.getResponseCode());
		}
	}
}
