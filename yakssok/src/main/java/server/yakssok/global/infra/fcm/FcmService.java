package server.yakssok.global.infra.fcm;


import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

@Service
public class FcmService {

	//잔소리, 칭찬 시 사용
	public void sendNotification(String token, String title, String body){
		try {
			Message message = Message.builder()
				.setToken(token)
				.setNotification(Notification.builder()
					.setTitle(title)
					.setBody(body)
					.build())
				.build();

			String response = FirebaseMessaging.getInstance().send(message);
		} catch (FirebaseMessagingException e) {
			throw new GlobalException(ErrorCode.FAILED_TO_SEND_NOTIFICATION);
		}
	}
}
