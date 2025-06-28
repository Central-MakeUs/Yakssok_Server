package server.yakssok.domain.feeback;

import jakarta.persistence.Entity;

public enum FeedBackType {
	PRAISE("칭찬"),
	NAG("잔소리");

	private String description;
	FeedBackType(String description) {
		this.description = description;
	}
}
