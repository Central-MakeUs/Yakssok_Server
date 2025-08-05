package server.yakssok.domain.notification.application.service.constants;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class NotificationTitleConstants {
	public static final String FEEDBACK_PRAISE_FORMAT_MUTUAL = "%s님의 %s님이 칭찬해요!";
	public static final String FEEDBACK_NAG_FORMAT_MUTUAL = "%s님의 %s님이 잔소리해요!";
	public static final String FEEDBACK_PRAISE_FORMAT_ONE_WAY = "%s님이 칭찬해요!";
	public static final String FEEDBACK_NAG_FORMAT_ONE_WAY = "%s님이 잔소리해요!";


	public static final String MEDICATION_FORMAT = "%s 챙길 시간이에요!";
	public static final String MEDICATION_REMINDER_FORMAT = "%s님~ %s 까먹었네요!";
	public static final String FRIEND_NOT_TAKEN_FORMAT = "%s님이 약을 안먹었어요!";
}
