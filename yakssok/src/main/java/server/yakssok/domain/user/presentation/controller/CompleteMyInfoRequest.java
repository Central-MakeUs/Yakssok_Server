package server.yakssok.domain.user.presentation.controller;

import jakarta.validation.constraints.NotNull;

public record CompleteMyInfoRequest(
	@NotNull
	String nickName
) {
}
