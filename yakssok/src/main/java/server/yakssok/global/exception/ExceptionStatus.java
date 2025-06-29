package server.yakssok.global.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ExceptionStatus {
    SUCCESS(HttpStatus.OK, 0, "성공적으로 처리되었습니다."),

    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, 1007, "유효하지 않은 카카오 token입니다."),
    ;

    private final HttpStatus httpStatus;
    private final int code;
    private final String message;

    ExceptionStatus(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
