package server.yakssok.domain.user.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record FindUserInviteCodeResponse(
	@Schema(description = "초대 코드", example = "YAKdSO1as")
	String inviteCode
) {
	public static FindUserInviteCodeResponse of(String inviteCode) {
		return new FindUserInviteCodeResponse(inviteCode);
	}
}
