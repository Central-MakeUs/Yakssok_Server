package server.yakssok.domain.notification.application.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NotTakenReminderTemplates {

	private static final String[] TITLE_TEMPLATES = {
		"%s 안 먹고 뭐 해요? 😤",
		"%s 까먹은 거 아니죠?",
		"%s 두고 하루 마무리할 순 없죠? 😣",
		"오늘 %s 먹을 마지막 기회예요… ⏳",
		"%s : “나 잊은 거 아니지? 🥺”",
		"오늘 %s 아직 안 먹었어요? 🙈"
	};

	private static final String[] BODY_TEMPLATES = {
		"얼른 챙기세요~",
		"약이 울고 있어요 💊😭",
		"놓치지 마세요 🥲",
		"오늘 끝나기 전에 꼭 챙기세요! ⏰",
		"얼른 먹어주세요 💕"
	};

	public static String randomTitle(String medicationName) {
		int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(TITLE_TEMPLATES.length);
		return String.format(TITLE_TEMPLATES[i], medicationName);
	}

	public static String randomBody() {
		int i = java.util.concurrent.ThreadLocalRandom.current().nextInt(BODY_TEMPLATES.length);
		return BODY_TEMPLATES[i];
	}
}
