package server.yakssok.global;

import static server.yakssok.global.SuccessCode.*;

import java.util.HashMap;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
	@Schema(description = "응답 코드", example = "0")
	private Integer code;
	@Schema(description = "응답 메시지", example = "성공적으로 처리되었습니다.")
	private String message;
	@Schema(description = "실제 응답 데이터")
	private T body;

	private ApiResponse(int code, String message, T body) {
		this.code = code;
		this.message = message;
		this.body = body;
	}

	public static <T> ApiResponse<T> success(T body) {
		return new ApiResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), body);
	}

	public static ApiResponse success() {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(SUCCESS.getCode(), SUCCESS.getMessage(), empty);
	}

	public static ApiResponse error(int code, String message) {
		HashMap<String, String> empty = new HashMap<>();
		return new ApiResponse<>(code, message, empty);
	}

	public static <T> ApiResponse<T> error(int code, T body, String message) {
		return new ApiResponse<>(code, message, body);
	}
}
