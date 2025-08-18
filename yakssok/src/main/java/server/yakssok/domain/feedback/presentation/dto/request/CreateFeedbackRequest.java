package server.yakssok.domain.feedback.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import server.yakssok.domain.feedback.domain.entity.Feedback;
import server.yakssok.domain.feedback.domain.entity.FeedbackType;
import server.yakssok.domain.user.domain.entity.User;
@Schema(description = "잔소리/칭찬 보내기 요청 DTO")
public record CreateFeedbackRequest(
	@Schema(description = "피드백 보낼 사용자 ID", example = "1")
	@NotNull
	Long receiverId,

	@Schema(description = "피드백 메세지", example = "약 좀 먹어라~~")
	@NotNull
	String message,
	
	@Schema(description = "피드백 유형", example = "praise", allowableValues = {"praise", "nag"})
	@NotNull
	String type
){
	public Feedback toFeedback(User sender, User receiver) {
		return Feedback.createFeedback(message, FeedbackType.from(type), sender, receiver);
	}
}

