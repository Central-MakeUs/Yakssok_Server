package server.yakssok.global.common.swagger;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(ApiErrorResponses.class)
public @interface ApiErrorResponse {
	int httpStatus();
	String message();
	int code();
}
