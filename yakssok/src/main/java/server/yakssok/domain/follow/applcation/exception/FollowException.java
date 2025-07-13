package server.yakssok.domain.follow.applcation.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

public class FollowException extends GlobalException {
	public FollowException(ErrorCode errorCode) {
		super(errorCode);
	}
}
