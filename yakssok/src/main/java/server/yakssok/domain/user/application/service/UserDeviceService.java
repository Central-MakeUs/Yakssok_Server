package server.yakssok.domain.user.application.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

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

	public void registerOrUpdateDevice(Long userId, RegisterDeviceRequest request) {
		User user = userService.getActiveUser(userId);
		Optional<UserDevice> existing = userDeviceRepository.findByUserIdAndDeviceId(userId, request.deviceId());

		if (existing.isPresent()) {
			existing.get().update(request.fcmToken(), request.alertOn());
		} else {
			UserDevice userDevice = request.toUserDevice(user);
			userDeviceRepository.save(userDevice);
		}
	}
}
