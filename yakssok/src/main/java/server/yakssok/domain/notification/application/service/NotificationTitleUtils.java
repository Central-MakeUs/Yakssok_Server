package server.yakssok.domain.notification.application.service;

import static server.yakssok.domain.notification.application.service.constants.NotificationTitleConstants.*;

import server.yakssok.domain.feeback.domain.entity.FeedbackType;

public class NotificationTitleUtils {

	public static String createFeedbackTitle(FeedbackType type, String senderName, String relationName) {
		return switch (type) {
			case PRAISE -> String.format(FEEDBACK_PRAISE_FORMAT, senderName, relationName);
			case NAG -> String.format(FEEDBACK_NAG_FORMAT, senderName, relationName);
		};
	}

	public static String createMedicationReminderTitle(String userName, String medicineName) {
		return String.format(MEDICATION_REMINDER_FORMAT, userName, medicineName);
	}

	public static String createFriendNotTakenAlarmTitle(String userName, String relationName) {
		String particle = chooseSubjectJosa(relationName);
		return String.format(FRIEND_NOT_TAKEN_FORMAT, userName, relationName, particle);
	}

	public static String chooseSubjectJosa(String word) {
		if (word == null || word.isEmpty()) return "";
		char lastChar = word.charAt(word.length() - 1);
		if (lastChar < 0xAC00 || lastChar > 0xD7A3) {
			return "가";
		}
		int baseCode = lastChar - 0xAC00;
		int jongseongIndex = baseCode % 28;
		return (jongseongIndex == 0) ? "가" : "이";
	}
}
