package server.yakssok.global.infra.fcm;


import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FcmService {

	public void sendNotification(String token, String title, String body){
		try {
			Message message = Message.builder()
				.setToken(token)
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.build();
			FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			log.warn("알림 전송 실패: {}", e.getMessage());
		}
	}

	public void sendNotifications(List<String> tokens, String title, String body) {
		if (tokens == null || tokens.isEmpty()) return;
		try {
			MulticastMessage message = MulticastMessage.builder()
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.addAllTokens(tokens)
				.build();
			FirebaseMessaging.getInstance().sendMulticast(message);
		} catch (FirebaseMessagingException e) {
			log.warn("알림 전송 실패: {}", e.getMessage());
		}
	}
}
