package server.yakssok.global.infra.oauth.exception;


import lombok.Getter;
import server.yakssok.global.exception.ErrorCode;
import server.yakssok.global.exception.GlobalException;

@Getter
public class OAuthException extends GlobalException {
	public OAuthException(ErrorCode errorCode) {
		super(errorCode);
	}
}
