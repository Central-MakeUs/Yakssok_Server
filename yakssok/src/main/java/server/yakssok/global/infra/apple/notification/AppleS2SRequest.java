package server.yakssok.global.infra.apple.notification;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public record AppleS2SRequest(
	@NotBlank
	@JsonProperty("payload")
	String payload
) {}