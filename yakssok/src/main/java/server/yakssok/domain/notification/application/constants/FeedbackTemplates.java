package server.yakssok.domain.notification.application.constants;

import server.yakssok.domain.feedback.domain.entity.FeedbackType;

public class FeedbackTemplates {

	public static final String FEEDBACK_PRAISE_TITLE_TEMPLATE = "%s님이 칭찬해요!";
	public static final String FEEDBACK_NAG_TITLE_TEMPLATE = "%s님이 잔소리해요!";

	public static String getTitle(FeedbackType type, String senderName) {
		return switch (type) {
			case PRAISE -> String.format(FEEDBACK_PRAISE_TITLE_TEMPLATE, senderName);
			case NAG -> String.format(FEEDBACK_NAG_TITLE_TEMPLATE, senderName);
		};
	}
}
