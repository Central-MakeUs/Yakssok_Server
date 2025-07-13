package server.yakssok.domain.follow.domain.entity;

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
public class Follow {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long followerId;
	private Long followingId;
	private String relationName;

	public static Follow create(Long followerId, Long followingId, String relationName) {
		return new Follow(null, followerId, followingId, relationName);
	}
}
