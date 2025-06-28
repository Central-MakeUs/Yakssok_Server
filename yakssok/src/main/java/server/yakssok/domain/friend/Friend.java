package server.yakssok.domain.friend;

import jakarta.persistence.Entity;

@Entity
public class Friend {
	private Long id;
	private Long userId;
	private Long friendId;
}
