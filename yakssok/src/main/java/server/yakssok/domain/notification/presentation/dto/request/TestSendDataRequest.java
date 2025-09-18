package server.yakssok.domain.notification.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "테스트 푸시 전송 요청 DTO")
public record TestSendDataRequest(

	@Schema(description = "FCM 디바이스 토큰", example = "bk3RNwTe3H0:CI2k_HHwgIpoDKCIZvvDMExUdFQ3P1...")
	String fcmToken,

	@Schema(description = "알림 제목", example = "알림 제목")
	String title,

	@Schema(description = "알림 본문", example = "알림 바디")
	String body,

	@Schema(description = "알림 사운드 타입", example = "FEEL_GOOD", allowableValues = {
		"FEEL_GOOD", "PILL_SHAKE", "SCOLD", "CALL", "VIBRATION"
	})
	String soundType
) {
}