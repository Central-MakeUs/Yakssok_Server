package server.yakssok.global.exception;


import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode implements ResponseCode{
    //oauth
    INVALID_OAUTH_TOKEN(HttpStatus.UNAUTHORIZED, 1000, "유효하지 않은 OAuth 토큰입니다."),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, 1001, "지원하지 않는 소셜 로그인 제공자입니다."),

    //auth
    DUPLICATE_USER(HttpStatus.BAD_REQUEST, 2000, "이미 가입된 회원입니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, 2001, "유효하지 않은 JWT 토큰입니다."),

    //user
    NOT_FOUND_USER(HttpStatus.NOT_FOUND, 3000, "존재하지 않는 회원입니다."),

    //common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9000, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 9001, "잘못된 입력 값입니다.");

    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
