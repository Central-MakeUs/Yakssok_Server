package server.yakssok.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.domain.user.presentation.dto.request.UpdateUserInfoRequest;
import server.yakssok.domain.user.presentation.dto.response.FindMyInfoResponse;
import server.yakssok.domain.user.presentation.dto.response.FindUserInfoResponse;
import server.yakssok.domain.user.presentation.dto.response.FindUserInviteCodeResponse;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public FindMyInfoResponse findMyInfo(Long userId) {
		User user = getUserByUserId(userId);
		return new FindMyInfoResponse(
			user.getNickName(),
			user.getProfileImageUrl()
		);
	}

	@Transactional
	public void updateUserInfo(Long userId, UpdateUserInfoRequest userInfoRequest) {
		User user = getUserByUserId(userId);
		user.updateInfo(
			userInfoRequest.nickname(),
			userInfoRequest.profileImageUrl()
		);
	}

	private User getUserByUserId(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
	}

	@Transactional(readOnly = true)
	public FindUserInviteCodeResponse findUserInviteCode(Long userId) {
		User user = getUserByUserId(userId);
		return new FindUserInviteCodeResponse(
			user.getInviteCode().getValue()
		);
	}

	@Transactional(readOnly = true)
	public FindUserInfoResponse findUserInfoByInviteCode(String inviteCode) {
		User user = userRepository.findByInviteCodeValue(inviteCode)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_INVITE_CODE));
		return new FindUserInfoResponse(
			user.getNickName(),
			user.getProfileImageUrl()
		);
	}

	public Long getUserIdByInviteCode(String inviteCode) {
		User user = userRepository.findByInviteCodeValue(inviteCode)
			.orElseThrow(() -> new UserException(ErrorCode.INVALID_INVITE_CODE));
		return user.getId();
	}
}
