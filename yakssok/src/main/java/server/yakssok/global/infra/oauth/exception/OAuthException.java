package server.yakssok.global.infra.oauth.exception;


import lombok.Getter;
import server.yakssok.global.exception.GlobalException;

@Getter
public class OAuthException extends GlobalException {
	public OAuthException(OAuthErrorCode responseCode) {
		super(responseCode);
	}
}
