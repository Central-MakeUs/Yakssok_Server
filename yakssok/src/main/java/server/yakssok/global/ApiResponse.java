package server.yakssok.global;

import java.util.HashMap;

import server.yakssok.global.exception.ExceptionStatus;

public class ApiResponse<T> {
	private Integer code;
	private String message;
	private T body;

	private ApiResponse(int code, String message, T body) {
		this.code = code;
		this.message = message;
		this.body = body;
	}

	public static <T> ApiResponse<T> success(T body) {
		return new ApiResponse<>(ExceptionStatus.SUCCESS.getCode(), ExceptionStatus.SUCCESS.getMessage(), body);
	}

	public static ApiResponse success() {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(ExceptionStatus.SUCCESS.getCode(), ExceptionStatus.SUCCESS.getMessage(), empty);
	}

	public static ApiResponse error(int code, String message) {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(code, message, empty);
	}

	public static <T> ApiResponse<T> error(int code, T body, String message) {
		return new ApiResponse<>(code, message, body);
	}
}
