package server.yakssok.domain.notification.application.constants;

public class NotTakenReportTemplates {
	private static final String[] TITLE_TEMPLATES = {
		"약 까먹은 %s 발견 👀",
		"%s님이 약을 안 먹었대요 😱",
		"똑똑👋 %s님이 약을 깜빡했어요!",
		"%s님 약 복용 실패 🚨",
		"%s님이 약을 놓쳐버렸어요 🥺"
	};

	private static final String[] BODY_TEMPLATES = {
		"얼른 잔소리 출격!",
		"따끔한 잔소리 날려주세요 ✨",
		"지금 바로 혼쭐내주세요 🤣",
		"잔소리 전송 미션 수행해주세요!",
		"귀여운 잔소리로 독촉해주세요! 🐣"
	};

	public static String randomTitle(String followingName) {
		int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(TITLE_TEMPLATES.length);
		return String.format(TITLE_TEMPLATES[i], followingName);
	}

	public static String randomBody() {
		int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(BODY_TEMPLATES.length);
		return BODY_TEMPLATES[i];
	}
}
