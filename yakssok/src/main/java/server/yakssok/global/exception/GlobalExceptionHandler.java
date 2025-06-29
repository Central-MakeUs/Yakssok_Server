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
	protected ResponseEntity<ApiResponse> badRequestException(YakssokException e) {
		ExceptionStatus exceptionStatus = e.getExceptionStatus();
		log.error("[{}] {} ({})", e.getClass().getSimpleName(), exceptionStatus.getMessage(), e.getStackTrace()[0]);
		ApiResponse apiResponse = ApiResponse.error(
			exceptionStatus.getCode(),
			e.getExceptionStatus().getMessage()
		);
		return ResponseEntity.status(e.getExceptionStatus().getHttpStatus()).body(apiResponse);
	}
}
