package server.yakssok.domain.friend.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public record FollowingMedicationStatusDetailResponse(
	int remainingCount,
	String nickName,
	String relationship,
	List<MedicationInfo> remainingMedications
) {
	public record MedicationInfo(
		String type,
		String name,
		LocalTime time
	) {}

	public static FollowingMedicationStatusDetailResponse of(
		String nickName,
		String relationship,
		List<MedicationScheduleDto> remainingMedications
	) {
		List<MedicationInfo> medicationInfos = remainingMedications.stream()
			.map(dto -> new MedicationInfo(
				dto.medicationType().name(),
				dto.medicationName(),
				dto.intakeTime()
			))
			.toList();

		return new FollowingMedicationStatusDetailResponse(
			medicationInfos.size(),
			nickName,
			relationship,
			medicationInfos
		);
	}
}
