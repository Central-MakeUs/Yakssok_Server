package server.yakssok.global.infra.fcm;


import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmService {

	public void sendNotification(String token, String title, String body) throws FirebaseMessagingException {
		Message message = Message.builder()
			.setToken(token)
			.setNotification(Notification.builder()
				.setTitle(title)
				.setBody(body)
				.build())
			.build();
		FirebaseMessaging.getInstance().send(message);
	}

	public BatchResponse sendMulticastNotifications(List<String> tokens, String title, String body) throws
		FirebaseMessagingException {
		MulticastMessage message = MulticastMessage.builder()
			.setNotification(Notification.builder()
				.setTitle(title)
				.setBody(body)
				.build())
			.addAllTokens(tokens)
			.build();
		return FirebaseMessaging.getInstance().sendEachForMulticast(message);
	}

	public void sendData(String token, String title, String body, String soundType) throws FirebaseMessagingException {
		Message message = Message.builder()
			.setToken(token)
			.putData("title", title)
			.putData("body", body)
			.putData("soundType", soundType)
			.setAndroidConfig(getAndroidConfig())
			.setApnsConfig(getApnsConfig())
			.build();
		FirebaseMessaging.getInstance().send(message);
	}

	private AndroidConfig getAndroidConfig() {
		return AndroidConfig.builder()
			.setPriority(AndroidConfig.Priority.HIGH)
			.build();
	}

	private ApnsConfig getApnsConfig() {
		return ApnsConfig.builder()
			.putHeader("apns-push-type", "background")
			.putHeader("apns-priority", "5")
			.setAps(
				com.google.firebase.messaging.Aps.builder()
					.setContentAvailable(true)
					.build()
			)
			.build();
	}
}
