package server.yakssok.global.exception;


import org.springframework.http.HttpStatus;

public interface ErrorCode {
    HttpStatus getHttpStatus();
    int getCode();
    String getMessage();
}
