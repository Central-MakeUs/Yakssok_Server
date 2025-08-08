package server.yakssok.domain.auth.presentation.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import server.yakssok.domain.auth.presentation.dto.request.OAuthLoginRequest;

public class AppleLoginRequestValidator implements ConstraintValidator<ValidAppleJoinRequest, OAuthLoginRequest> {
	@Override
	public boolean isValid(OAuthLoginRequest value, ConstraintValidatorContext context) {
		if ("apple".equalsIgnoreCase(value.oauthType())) {
			boolean nonceValid = value.nonce() != null && !value.nonce().isBlank();
			return nonceValid;
		}
		return true;
	}
}


