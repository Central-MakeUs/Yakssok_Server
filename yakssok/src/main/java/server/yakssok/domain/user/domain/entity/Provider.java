package server.yakssok.domain.user.domain.entity;

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
