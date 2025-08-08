package server.yakssok.domain.auth.presentation.dto.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = AppleLoginRequestValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAppleJoinRequest {
	String message() default "애플 로그인 시 nonce는 필수입니다.";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
}