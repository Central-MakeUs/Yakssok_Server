package server.yakssok.domain.notification.application.service;

import server.yakssok.domain.feeback.domain.entity.FeedbackType;

public class NotificationTitleUtils {
	private static final String FEEDBACK_PRAISE_FORMAT = "%s님이 보낸 칭찬!";
	private static final String FEEDBACK_NAG_FORMAT = "%s님이 보낸 잔소리!";
	private static final String FEEDBACK_DEFAULT_FORMAT = "%s님이 보낸 피드백!";

	private static final String MEDICATION_REMINDER_FORMAT = "%s님~ %s 안 먹었어요!";
	private static final String FRIEND_NOT_TAKEN_FORMAT = "%s님의 %s가 약을 안먹었어요";


	public static String createFeedbackTitle(FeedbackType type, String senderName) {
		switch(type) {
			case PRAISE:
				return String.format(FEEDBACK_PRAISE_FORMAT, senderName);
			case NAG:
				return String.format(FEEDBACK_NAG_FORMAT, senderName);
			default:
				return String.format(FEEDBACK_DEFAULT_FORMAT, senderName);
		}
	}

	public static String createMedicationReminderTitle(String userName, String medicineName) {
		return String.format(MEDICATION_REMINDER_FORMAT, userName, medicineName);
	}

	public static String createFriendNotTakenAlarmTitle(String userName, String mateRole) {
		return String.format(FRIEND_NOT_TAKEN_FORMAT, userName, mateRole);
	}
}
