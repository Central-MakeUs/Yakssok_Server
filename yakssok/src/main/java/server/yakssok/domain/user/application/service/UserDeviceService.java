package server.yakssok.domain.user.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.domain.user.presentation.dto.request.RegisterDeviceRequest;

@Service
@RequiredArgsConstructor
public class UserDeviceService {
	private final UserDeviceRepository userDeviceRepository;
	private final UserService userService;

	@Transactional
	public void registerOrUpdateDevice(Long userId, RegisterDeviceRequest request) {
		User user = userService.getActiveUser(userId);

		Optional<UserDevice> userDevice = userDeviceRepository.findByFcmToken(request.fcmToken());
		if (userDevice.isPresent()) {
			reassignAndUpdate(userDevice.get(), user, request);
			return;
		}

		Optional<UserDevice> existing = userDeviceRepository.findByDeviceId(request.deviceId());
		if (existing.isPresent()) {
			reassignAndUpdate(existing.get(), user, request);
			return;
		}

		//새로운 기기면 새로 등록
		registerNewDevice(request, user);
	}

	private void registerNewDevice(RegisterDeviceRequest request, User user) {
		UserDevice newUserDevice = request.toUserDevice(user);
		userDeviceRepository.save(newUserDevice);
	}

	private void reassignAndUpdate(UserDevice userDevice, User user, RegisterDeviceRequest request) {
		userDevice.reassignAndUpdate(user, request.alertOn(), request.deviceId(), request.fcmToken());
	}
}
