package server.yakssok.global.infra.oauth;

public interface OAuthUserResponse {
	String getId();
	String getProfileImageUrl();
	String getRefreshToken();
}
