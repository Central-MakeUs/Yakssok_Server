package server.yakssok.domain;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class TestController {
	@GetMapping("/log-error")
	public String triggerSlackError() {
		log.error("🔥 슬랙 전송 테스트 from DEV 서버");
		return "Slack 전송 테스트 완료!";
	}
}
