package server.yakssok.domain.user;

import lombok.Getter;

@Getter
public enum Provider {
	//카카오, 애플
	KAKAO("kakao"),
	APPLE("apple");

	private String providerName;

	Provider(String providerName) {
		this.providerName = providerName;
	}
}
