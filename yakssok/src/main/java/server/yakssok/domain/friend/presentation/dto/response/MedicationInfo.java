package server.yakssok.domain.friend.presentation.dto.response;

import java.time.LocalTime;

import io.swagger.v3.oas.annotations.media.Schema;

public record MedicationInfo(
	@Schema(description = "약 종류", example = "CHRONIC")
	String type,
	@Schema(description = "약 이름", example = "타이레놀")
	String name,
	@Schema(description = "복용 시간 (HH:mm:ss)", type = "string", format="HH:mm:ss", example = "08:00:00")
	LocalTime time
) {

}
