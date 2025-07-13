package server.yakssok.domain.friend.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Friend {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private Long followingFriendId;
	private String relationName;

	public static Friend create(Long userId, Long followingFriendId, String relationName) {
		return new Friend(userId, followingFriendId, relationName);
	}

	private Friend(Long userId, Long followingFriendId, String relationName) {
		this.userId = userId;
		this.followingFriendId = followingFriendId;
		this.relationName = relationName;
	}
}
