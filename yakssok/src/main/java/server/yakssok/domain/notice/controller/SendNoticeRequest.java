package server.yakssok.domain.notice.controller;

import io.swagger.v3.oas.annotations.media.Schema;

public record SendNoticeRequest(
	@Schema(description = "유저 id", example = "1")
	Long userId,

	@Schema(description = "알림 제목", example = "알림 제목")
	String title,

	@Schema(description = "알림 본문", example = "알림 바디")
	String body
) {
}
