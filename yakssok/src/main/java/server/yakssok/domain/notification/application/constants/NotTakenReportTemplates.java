package server.yakssok.domain.notification.application.constants;

public class NotTakenReportTemplates {
	public static final String TITLE_TEMPLATE = "%s님이 약을 안먹었어요!";
	public static final String BODY_TEMPLATE = "잔소리 장전 완료. 잔소리 보내봐요 우리.";

	public static String getTitle(String followingNickName) {
		return String.format(TITLE_TEMPLATE, followingNickName);
	}

	public static String getBody() {
		return BODY_TEMPLATE;
	}
}
