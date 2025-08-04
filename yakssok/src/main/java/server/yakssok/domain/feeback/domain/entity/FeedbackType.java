package server.yakssok.domain.feeback.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import server.yakssok.domain.notification.domain.entity.NotificationType;

public enum FeedbackType {

	FEEDBACK_PRAISE,
	FEEDBACK_NAG;

	public static FeedbackType from(String name) {
		try {
			return FeedbackType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new FeedbackException(INVALID_INPUT_VALUE);
		}
	}

	public NotificationType toNotificationType() {
		return switch(this) {
			case FEEDBACK_PRAISE -> NotificationType.FEEDBACK_PRAISE;
			case FEEDBACK_NAG -> NotificationType.FEEDBACK_NAG;
		};
	}
}
