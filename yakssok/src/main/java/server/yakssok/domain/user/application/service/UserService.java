package server.yakssok.domain.user.application.service;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;
import server.yakssok.domain.user.presentation.dto.request.UpdateUserInfoRequest;
import server.yakssok.domain.user.presentation.dto.response.FindUserInfoResponse;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;

	@Transactional
	public FindUserInfoResponse findUserInfo(Long userId) {
		User user = getUser(userId);
		return new FindUserInfoResponse(
			user.getNickName(),
			user.getProfileImageUrl()
		);
	}

	@Transactional
	public void updateUserInfo(Long userId, UpdateUserInfoRequest userInfoRequest) {
		User user = getUser(userId);
		user.updateInfo(
			userInfoRequest.nickname(),
			userInfoRequest.profileImageUrl()
		);
	}

	public User getUser(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
	}
}
