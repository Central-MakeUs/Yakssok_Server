package server.yakssok.domain.user.application.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
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

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final UserDeletionService userDeletionService;
	private final MedicationRepository medicationRepository;
	private final FriendRepository friendRepository;
	private final AuthService authService;

	@Transactional
	public FindMyInfoResponse findMyInfo(Long userId) {
		User user = getActiveUser(userId);
		int medicationCount = medicationRepository.countByUserId(userId);
		int followingCount = friendRepository.countByUserId(userId);
		return FindMyInfoResponse.of(user, medicationCount, followingCount);
	}

	@Transactional
	public void updateUserInfo(Long userId, UpdateUserInfoRequest userInfoRequest) {
		User user = getActiveUser(userId);
		user.updateInfo(
			userInfoRequest.nickname(),
			userInfoRequest.profileImageUrl()
		);
	}

	public User getActiveUser(Long userId) {
		return userRepository.findByIdAndIsDeletedFalse(userId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
	}

	@Transactional(readOnly = true)
	public FindUserInviteCodeResponse findUserInviteCode(Long userId) {
		User user = getActiveUser(userId);
		return FindUserInviteCodeResponse.of(user.getInviteCode().getValue());
	}

	@Transactional(readOnly = true)
	public FindUserInfoResponse findUserInfoByInviteCode(String inviteCode) {
		User user = userRepository.findByInviteCodeValue(inviteCode)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_INVITE_CODE));
		return FindUserInfoResponse.from(user);
	}

	public User getUserIdByInviteCode(String inviteCode) {
		User user = userRepository.findByInviteCodeValue(inviteCode)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_INVITE_CODE));
		return user;
	}

	@Transactional
	public void deleteUser(Long userId) {
		User user = getActiveUser(userId);
		authService.unlinkOAuth(user);
		userDeletionService.deleteUser(user);
	}

	@Transactional
	public void initializeMyInfo(Long userId, CompleteMyInfoRequest request) {
		User activeUser = getActiveUser(userId);
		activeUser.initializeUserInfo(request.nickName());
	}
}
