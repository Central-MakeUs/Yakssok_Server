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
    INVALID_INVITE_CODE(HttpStatus.BAD_REQUEST, 3001, "유효하지 않은 초대 코드입니다."),

    //follow, feedback
    ALREADY_FRIEND(HttpStatus.BAD_REQUEST, 4000, "이미 친구로 등록된 사용자입니다."),
    CANNOT_FOLLOW_SELF(HttpStatus.BAD_REQUEST, 4001, "자기 자신을 친구로 추가할 수 없습니다."),

    // medication
    NOT_FOUND_MEDICATION(HttpStatus.NOT_FOUND, 5000, "존재하지 않는 복약 스케줄입니다."),
    NOT_FOUND_MEDICATION_SCHEDULE(HttpStatus.NOT_FOUND, 5001, "존재하지 않는 복약 스케줄입니다."),


    //common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 9000, "서버 오류가 발생했습니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, 9001, "잘못된 입력 값입니다."),

    // image
    FAILED_FILE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, 9100, "이미지 업로드에 실패했습니다."),
    FAILED_FILE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, 9101, "이미지 삭제에 실패했습니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, 9102, "지원하지 않는 파일 타입입니다."),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, 9103, "지원하지 않는 파일 확장자입니다."),
    ;





    private final HttpStatus httpStatus;
    private final Integer code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, Integer code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
