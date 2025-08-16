package server.yakssok.domain.notification.presentation.controller;

public record TestSendDataRequest(
	String fcmToken,
	String title,
	String body,
	String soundType
) {
}