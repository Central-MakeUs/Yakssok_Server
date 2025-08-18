package server.yakssok.domain.user.application.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

public class UserException extends GlobalException {
	public UserException(ErrorCode errorCode) {
		super(errorCode);
	}
}
