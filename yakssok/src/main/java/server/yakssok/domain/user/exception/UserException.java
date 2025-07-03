package server.yakssok.domain.user.exception;

import server.yakssok.global.exception.GlobalException;

public class UserException extends GlobalException {
	public UserException(UserErrorCode responseCode) {
		super(responseCode);
	}
}
