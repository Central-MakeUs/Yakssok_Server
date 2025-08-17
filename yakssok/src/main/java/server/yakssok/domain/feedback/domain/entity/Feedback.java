package server.yakssok.domain.feedback.domain.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;
import server.yakssok.domain.user.domain.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String message;
	@Enumerated(EnumType.STRING)
	private FeedbackType feedbackType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sender_id")
	private User sender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id")
	private User receiver;

	public static Feedback createFeedback(
		String message,
		FeedbackType feedbackType,
		User sender,
		User receiver
	) {
		Feedback feedback = new Feedback();
		feedback.message = message;
		feedback.feedbackType = feedbackType;
		feedback.sender = sender;
		feedback.receiver = receiver;
		return feedback;
	}

}
