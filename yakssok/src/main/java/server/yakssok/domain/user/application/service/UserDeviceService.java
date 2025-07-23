package server.yakssok.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.presentation.dto.CreateFcmRequest;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class UserDeviceService {
	private final UserDeviceRepository userDeviceRepository;

	@Transactional
	public void saveFcmToken(Long userId, CreateFcmRequest createFcmRequest) {
		UserDevice userDevice = userDeviceRepository.findByUserId(userId)
			.orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));
		userDevice.updateFcmToken(createFcmRequest.fcmToken());
	}
}
