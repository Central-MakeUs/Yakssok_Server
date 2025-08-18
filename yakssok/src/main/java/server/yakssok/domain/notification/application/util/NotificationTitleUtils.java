package server.yakssok.domain.notification.application.util;

import static server.yakssok.domain.notification.application.constants.NotificationTitleConstants.*;

import server.yakssok.domain.feedback.domain.entity.FeedbackType;

public class NotificationTitleUtils {

	public static String createFeedbackTitleMutual(FeedbackType type, String receiverName, String relationName) {
		return switch (type) {
			case PRAISE -> String.format(FEEDBACK_PRAISE_FORMAT_MUTUAL, receiverName, relationName);
			case NAG -> String.format(FEEDBACK_NAG_FORMAT_MUTUAL, receiverName, relationName);
		};
	}

	public static String createFeedbackTitleOneWay(FeedbackType type, String senderName) {
		return switch (type) {
			case PRAISE -> String.format(FEEDBACK_PRAISE_FORMAT_ONE_WAY, senderName);
			case NAG -> String.format(FEEDBACK_NAG_FORMAT_ONE_WAY, senderName);
		};
	}

	public static String createMedicationReminderTitle(String userName, String medicineName) {
		return String.format(MEDICATION_REMINDER_FORMAT, userName, medicineName);
	}

	public static String createFriendNotTakenAlarmTitle(String followingNickName) {
		return String.format(FRIEND_NOT_TAKEN_FORMAT, followingNickName);
	}

	public static String createMedicationTitle(String medicineName) {
		return String.format(MEDICATION_FORMAT, medicineName);
	}
}
