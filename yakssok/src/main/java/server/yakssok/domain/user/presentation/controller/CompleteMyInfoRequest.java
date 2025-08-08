package server.yakssok.domain.user.presentation.controller;

import jakarta.validation.constraints.NotEmpty;

public record CompleteMyInfoRequest(
	@NotEmpty
	String nickName
) {
}
