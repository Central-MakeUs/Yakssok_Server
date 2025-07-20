package server.yakssok.domain.feeback.domain.entity;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class FeedbackException extends GlobalException {

	public FeedbackException(ResponseCode responseCode) {
		super(responseCode);
	}
}
