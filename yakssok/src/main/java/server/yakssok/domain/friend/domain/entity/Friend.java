package server.yakssok.domain.friend.domain.entity;

import jakarta.persistence.Entity;

@Entity
public class Friend {
	private Long id;
	private Long userId;
	private Long friendId;
}
