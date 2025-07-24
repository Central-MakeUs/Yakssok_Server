package server.yakssok.domain.user.presentation.dto.request;

import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;

public record RegisterDeviceRequest(
	String deviceId,
	String fcmToken,
	boolean alertOn
) {
	public UserDevice toUserDevice(User user) {
		return UserDevice.createUserDevice(
			user,
			deviceId,
			fcmToken,
			alertOn
		);
	}
}