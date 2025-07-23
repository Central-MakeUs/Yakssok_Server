package server.yakssok.domain.notification.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notification.presentation.dto.CreateFcmRequest;
import server.yakssok.domain.user.application.exception.UserException;
import server.yakssok.domain.user.domain.entity.UserDevice;
import server.yakssok.domain.user.domain.repository.UserDeviceRepository;
import server.yakssok.global.exception.ErrorCode;

@Service
@Transactional
@RequiredArgsConstructor
public class FcmTokenService {
	private final UserDeviceRepository userDeviceRepository;

	public void saveFcmToken(Long userId, CreateFcmRequest createFcmRequest) {
		UserDevice userDevice = userDeviceRepository.findByUserId(userId)
			.orElseThrow(() -> new UserException(ErrorCode.INTERNAL_SERVER_ERROR));
		userDevice.updateFcmToken(createFcmRequest.fcmToken());
	}
}
