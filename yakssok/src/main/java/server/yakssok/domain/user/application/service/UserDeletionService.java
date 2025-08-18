package server.yakssok.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.auth.application.service.RefreshTokenService;
import server.yakssok.domain.friend.domain.repository.FriendRepository;
import server.yakssok.domain.medication.application.service.MedicationService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
@Service
@RequiredArgsConstructor
public class UserDeletionService {
	private final FriendRepository friendRepository;
	private final MedicationService medicationService;
	private final RefreshTokenService refreshTokenService;
	private final UserDeviceRepository userDeviceRepository;

	@Transactional
	public void deleteUser(User user) {
		Long userId = user.getId();
		deleteAllUserInfo(user, userId);
		deleteUserAllMedications(userId);
		deleteAllFriend(userId);
	}

	private void deleteUserAllMedications(Long userId) {
		medicationService.deleteAllByUserId(userId);
	}

	private void deleteAllUserInfo(User user, Long userId) {
		user.deactivate();
		refreshTokenService.deleteRefreshToken(userId);
		userDeviceRepository.deleteAllByUserId(userId);
	}

	private void deleteAllFriend(Long userId) {
		friendRepository.deleteAllByUserId(userId);
		friendRepository.deleteAllByFollowingId(userId);
	}
}
