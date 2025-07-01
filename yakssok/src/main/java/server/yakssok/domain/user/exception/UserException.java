package server.yakssok.domain.user.exception;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class UserException extends GlobalException {
	public UserException(ResponseCode responseCode) {
		super(responseCode);
	}
}
