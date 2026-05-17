package server.yakssok.domain.notice.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notice.controller.SendNoticeRequest;
import server.yakssok.domain.notification.application.service.PushService;
import server.yakssok.domain.notification.presentation.dto.NotificationAllDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class NoticeService {
	private final PushService pushService;

	public void sendNoticeToAll(SendNoticeRequest request) {
		NotificationAllDTO notificationAllDTO = NotificationAllDTO.fromNotice(request.title(), request.body());
		pushService.sendAllNotification(notificationAllDTO);
	}
}
