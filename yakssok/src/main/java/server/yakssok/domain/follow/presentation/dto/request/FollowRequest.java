package server.yakssok.domain.follow.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(description = "지인 팔로우")
public record FollowRequest(
	@Schema(description = "팔로우 할 사람의 초대 코드", example = "AWsaZP8cq")
	String inviteCode,
	@Schema(description = "관계명", example = "엄마")
	String relationName
) {
}
