package server.yakssok.domain.user.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.YakssokException;

public class UserException extends YakssokException {
	public UserException(ErrorCode errorCode) {
		super(errorCode);
	}
}
