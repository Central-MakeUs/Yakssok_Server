package server.yakssok.domain.notification.application.service;

import server.yakssok.domain.feeback.domain.entity.FeedbackType;

public class NotificationTitleUtils {
	public static String createFeedbackTitle(FeedbackType type, String senderName) {
		switch(type) {
			case PRAISE: return senderName + "님이 보낸 칭찬!";
			case NAG:    return senderName + "님이 보낸 잔소리!";
			default:     return senderName + "님이 보낸 피드백!";
		}
	}
}
