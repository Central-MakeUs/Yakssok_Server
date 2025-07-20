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
public class Friend {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private Long friendId;
	private String relationName;

	public static Friend create(Long userId, Long friendId, String relationName) {
		return new Friend(userId, friendId, relationName);
	}

	private Friend(Long userId, Long friendId, String relationName) {
		this.userId = userId;
		this.friendId = friendId;
		this.relationName = relationName;
	}
}
