package server.yakssok.domain.feeback.domain.entity;

public enum FeedBackType {
	PRAISE("칭찬"),
	NAG("잔소리");

	private String description;
	FeedBackType(String description) {
		this.description = description;
	}
}
