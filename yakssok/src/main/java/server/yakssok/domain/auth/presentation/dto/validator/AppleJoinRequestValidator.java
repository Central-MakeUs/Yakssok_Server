package server.yakssok.domain.auth.presentation.dto.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import server.yakssok.domain.auth.presentation.dto.request.JoinRequest;

public class AppleJoinRequestValidator implements ConstraintValidator<ValidAppleJoinRequest, JoinRequest> {
	@Override
	public boolean isValid(JoinRequest value, ConstraintValidatorContext context) {
		if ("apple".equalsIgnoreCase(value.oauthType())) {
			boolean nonceValid = value.nonce() != null && !value.nonce().isBlank();
			return nonceValid;
		}
		return true;
	}
}


