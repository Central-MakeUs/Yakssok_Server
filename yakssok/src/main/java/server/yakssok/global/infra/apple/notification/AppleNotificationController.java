package server.yakssok.global.infra.apple.notification;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apple")
public class AppleNotificationController {

	private final AppleNotificationService appleNotificationService;
	@PostMapping("/notifications")
	public ResponseEntity<String> receive(@Valid @RequestBody AppleS2SRequest request) {
		try {
			appleNotificationService.handle(request.payload());
			return ResponseEntity.ok("OK");
		} catch (SecurityException e) {
			return ResponseEntity.status(401).body("invalid signature");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("internal error");
		}
	}
}