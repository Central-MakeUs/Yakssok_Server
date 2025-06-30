package server.yakssok.global;

import java.util.HashMap;


public class ApiResponse<T> {
	private Integer code;
	private String message;
	private T body;
	private static final String SUCCESS_MESSAGE = "성공적으로 처리되었습니다.";
	private static final int SUCCESS_CODE = 0;

	private ApiResponse(int code, String message, T body) {
		this.code = code;
		this.message = message;
		this.body = body;
	}

	public static <T> ApiResponse<T> success(T body) {
		return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, body);
	}

	public static ApiResponse success() {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(SUCCESS_CODE, SUCCESS_MESSAGE, empty);
	}

	public static ApiResponse error(int code, String message) {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(code, message, empty);
	}

	public static <T> ApiResponse<T> error(int code, T body, String message) {
		return new ApiResponse<>(code, message, body);
	}
}
