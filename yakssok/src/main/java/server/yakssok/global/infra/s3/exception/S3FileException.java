package server.yakssok.global.infra.s3.exception;

import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

public class S3FileException extends GlobalException {
	public S3FileException(ResponseCode responseCode) {
		super(responseCode);
	}
}
