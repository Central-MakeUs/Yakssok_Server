package server.yakssok.domain.feeback.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;


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
}
