package server.yakssok.global.infra.oauth.exception;


import lombok.Getter;
import server.yakssok.global.exception.GlobalException;
import server.yakssok.global.exception.ResponseCode;

@Getter
public class OAuthException extends GlobalException {
	public OAuthException(ResponseCode responseCode) {
		super(responseCode);
	}
}
