package server.yakssok.domain.notification.application.service.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NotificationTitleConstants {
	public static final String FEEDBACK_PRAISE_FORMAT = "%s님의 %s님이 칭찬해요!";
	public static final String FEEDBACK_NAG_FORMAT = "%s님의 %s님이 잔소리해요!";

	public static final String MEDICATION_REMINDER_FORMAT = "%s님~ %s 안 먹었어요!";
	public static final String FRIEND_NOT_TAKEN_FORMAT = "%s님의 %s%s 약을 안먹었어요";

}
