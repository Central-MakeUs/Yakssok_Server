package server.yakssok.domain.feeback.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import server.yakssok.domain.user.domain.entity.User;

@Entity
public class FeedBack {
	private Long id;

	private User sender;
	private User receiver;

	private String message;
	private FetchType fetchType;
	private LocalDateTime createdAt;
}
