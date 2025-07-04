package server.yakssok.global.exception;


import org.springframework.http.HttpStatus;

public interface ResponseCode {
    HttpStatus getHttpStatus();
    Integer getCode();
    String getMessage();
}
