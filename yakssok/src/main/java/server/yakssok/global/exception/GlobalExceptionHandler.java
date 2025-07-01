package server.yakssok.global.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import server.yakssok.global.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(GlobalException.class)
	protected ResponseEntity<ApiResponse> yakssokExceptionHandler(GlobalException e) {
		ResponseCode responseCode = e.getResponseCode();
		log.error("[{}] {} ({})", e.getClass().getSimpleName(), responseCode.getMessage(), e.getStackTrace()[0]);
		ApiResponse apiResponse = ApiResponse.error(
			responseCode.getCode(),
			responseCode.getMessage()
		);
		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(apiResponse);
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ApiResponse> runtimeExceptionHandler(RuntimeException e) {
		log.error("[{}] {} ({})", e.getClass().getSimpleName(), e.getMessage(), e.getStackTrace()[0]);

		ResponseCode responseCode = GlobalErrorCode.INTERNAL_SERVER_ERROR;
		ApiResponse apiResponse = ApiResponse.error(
			responseCode.getCode(),
			responseCode.getMessage()
		);

		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(apiResponse);
	}
}
