package server.yakssok.domain.notification.application.service;

import static server.yakssok.domain.notification.application.service.NotificationTitleConstants.*;

import server.yakssok.domain.feeback.domain.entity.FeedbackType;

public class NotificationTitleUtils {

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
