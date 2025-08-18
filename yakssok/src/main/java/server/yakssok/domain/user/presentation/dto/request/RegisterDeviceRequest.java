package server.yakssok.domain.user.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.entity.UserDevice;

public record RegisterDeviceRequest(
	@NotNull
	String deviceId,
	@NotNull
	String fcmToken,
	@NotNull
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