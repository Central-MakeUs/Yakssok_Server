package server.yakssok.domain.feeback.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import server.yakssok.domain.feeback.domain.entity.Feedback;
import server.yakssok.domain.feeback.domain.entity.FeedbackType;
import server.yakssok.domain.user.domain.entity.User;
@Schema(description = "잔소리/칭찬 보내기 요청 DTO")
public record CreateFeedbackRequest(
	@Schema(description = "피드백 보낼 사용자 ID", example = "1")
	Long receiverId,
	@Schema(description = "피드백 메세지", example = "약 좀 먹어라~~")
	String message,
	@Schema(description = "피드백 유형", example = "praise", allowableValues = {"praise", "nag"})
	String type
){
	public Feedback toFeedback(User sender, User receiver) {
		return Feedback.createFeedback(message, FeedbackType.from(type), sender, receiver);
	}
}

