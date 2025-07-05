package server.yakssok.domain.user.presentation.dto.response;

public record FindUserInfoResponse(
	String nickname,
	String profileImageUrl
) {
}
