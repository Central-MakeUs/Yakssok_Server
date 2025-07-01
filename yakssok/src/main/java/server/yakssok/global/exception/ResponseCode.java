package server.yakssok.global.exception;


import org.springframework.http.HttpStatus;

public interface ResponseCode {
    HttpStatus getHttpStatus();
    int getCode();
    String getMessage();
}
