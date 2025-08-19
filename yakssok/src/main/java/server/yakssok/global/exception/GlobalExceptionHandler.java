package server.yakssok.global.exception;

import static server.yakssok.global.exception.ErrorCode.*;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import lombok.extern.slf4j.Slf4j;
import server.yakssok.global.common.reponse.ApiResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler({
		HttpRequestMethodNotSupportedException.class,
		HttpMessageNotReadableException.class,
		MissingServletRequestParameterException.class,
		MethodArgumentNotValidException.class,
		MethodArgumentTypeMismatchException.class
	})
	protected ResponseEntity<ApiResponse> badRequestHandler(Exception e) {
		log.warn("[{}] {} ({})", e.getClass().getSimpleName(), e.getMessage(), e.getStackTrace()[0]);
		ResponseCode responseCode = ErrorCode.INVALID_INPUT_VALUE;
		ApiResponse apiResponse = ApiResponse.error(
			responseCode.getCode(),
			responseCode.getMessage()
		);

		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(apiResponse);
	}


	@ExceptionHandler(GlobalException.class)
	protected ResponseEntity<ApiResponse> yakssokExceptionHandler(GlobalException e) {
		ResponseCode responseCode = e.getResponseCode();
		log.warn("[{}] {} ({})", e.getClass().getSimpleName(), responseCode.getMessage(), e.getStackTrace()[0]);
		ApiResponse apiResponse = ApiResponse.error(
			responseCode.getCode(),
			responseCode.getMessage()
		);
		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(apiResponse);
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse> handleMaxUpload(MaxUploadSizeExceededException e) {
		ApiResponse apiResponse = ApiResponse.error(
			FILE_SIZE_EXCEEDED.getCode(),
			FILE_SIZE_EXCEEDED.getMessage()
		);
		return ResponseEntity
			.status(HttpStatus.PAYLOAD_TOO_LARGE)
			.body(apiResponse);
	}

	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<ApiResponse> runtimeExceptionHandler(RuntimeException e) {
		log.error("[{}] {} ({})", e.getClass().getSimpleName(), e.getMessage(), e.getStackTrace()[0]);
		e.getStackTrace();
		ResponseCode responseCode = ErrorCode.INTERNAL_SERVER_ERROR;
		ApiResponse apiResponse = ApiResponse.error(
			responseCode.getCode(),
			responseCode.getMessage()
		);

		return ResponseEntity
			.status(responseCode.getHttpStatus())
			.body(apiResponse);
	}
}
