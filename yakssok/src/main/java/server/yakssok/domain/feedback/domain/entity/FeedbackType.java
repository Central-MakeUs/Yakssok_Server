package server.yakssok.domain.feedback.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import server.yakssok.domain.notification.domain.entity.NotificationType;

public enum FeedbackType {

	PRAISE,
	NAG;

	public static FeedbackType from(String name) {
		try {
			return FeedbackType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new FeedbackException(INVALID_INPUT_VALUE);
		}
	}

	public NotificationType toNotificationType() {
		return switch(this) {
			case PRAISE -> NotificationType.FEEDBACK_PRAISE;
			case NAG -> NotificationType.FEEDBACK_NAG;
		};
	}
}
