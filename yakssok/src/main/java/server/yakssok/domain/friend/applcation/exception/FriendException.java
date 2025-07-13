package server.yakssok.domain.friend.applcation.exception;

import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

public class FriendException extends GlobalException {
	public FriendException(ErrorCode errorCode) {
		super(errorCode);
	}
}
