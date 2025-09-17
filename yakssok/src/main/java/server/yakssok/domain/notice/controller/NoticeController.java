package server.yakssok.domain.notice.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.notice.NoticeService;
import server.yakssok.global.common.reponse.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
@Tag(name = "Notice", description = "공지 API")
public class NoticeController {
	private final NoticeService noticeService;

	@Operation(summary = "공지 발송 (관리자 전용)", description = "공지 내용 푸시 알림 전송")
	@PostMapping("/send")
	public ApiResponse<Void> sendNotice(@Valid @RequestBody SendNoticeRequest request) {
		noticeService.sendNotice(request);
		return ApiResponse.success();
	}
}
