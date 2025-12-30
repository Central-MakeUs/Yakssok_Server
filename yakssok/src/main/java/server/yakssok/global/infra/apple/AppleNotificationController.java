package server.yakssok.global.infra.apple;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apple")
public class AppleNotificationController {

	private final AppleNotificationService appleNotificationService;

	@PostMapping("/notifications")
	public ResponseEntity<String> receive(@RequestBody Map<String, Object> body) {
		Object payloadObj = body.get("payload");
		if (!(payloadObj instanceof String payload) || payload.isBlank()) {
			return ResponseEntity.badRequest().body("missing payload");
		}

		try {
			appleNotificationService.handle(payload);
			return ResponseEntity.ok("OK");
		} catch (SecurityException e) {
			// 서명 검증 실패 등: 위조/부적절 요청
			return ResponseEntity.status(401).body("invalid signature");
		} catch (Exception e) {
			// 내부 처리 실패: 애플이 재시도할 수도 있으니 로깅 권장
			return ResponseEntity.status(500).body("internal error");
		}
	}
}