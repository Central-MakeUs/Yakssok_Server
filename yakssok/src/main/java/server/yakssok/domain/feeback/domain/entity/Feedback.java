package server.yakssok.domain.feeback.domain.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.user.domain.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Feedback {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JoinColumn(name = "sender_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User sender;

	@JoinColumn(name = "receiver_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User receiver;

	private String message;
	private FetchType fetchType;
	private LocalDateTime createdAt;
}
