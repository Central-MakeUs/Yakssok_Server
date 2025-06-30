package server.yakssok.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import server.yakssok.global.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(YakssokException.class)
	protected ResponseEntity<ApiResponse> YakssokExceptionHandler(YakssokException e) {
		ErrorCode errorCode = e.getErrorCode();
		log.error("[{}] {} ({})", e.getClass().getSimpleName(), errorCode.getMessage(), e.getStackTrace()[0]);
		ApiResponse apiResponse = ApiResponse.error(
			errorCode.getCode(),
			errorCode.getMessage()
		);
		ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
		return ResponseEntity
			.status(errorCode.getHttpStatus())
			.body(apiResponse);
	}
}
