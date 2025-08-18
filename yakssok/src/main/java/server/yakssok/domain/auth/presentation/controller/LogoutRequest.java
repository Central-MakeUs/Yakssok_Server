package server.yakssok.domain.auth.presentation.controller;

import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
	@NotNull
	String deviceId
) {
}
