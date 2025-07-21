package server.yakssok.domain.friend.presentation.dto.response;

import java.time.LocalTime;
import java.util.List;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public record FollowingMedicationStatusDetailResponse(
	int notTakenCount,
	String nickName,
	String relationship,
	List<MedicationInfo> notTakenMedications
) {
	public record MedicationInfo(
		String type,
		String name,
		LocalTime time
	) {}

	public static FollowingMedicationStatusDetailResponse of(
		String nickName,
		String relationship,
		List<MedicationScheduleDto> notTakenMedications
	) {
		List<MedicationInfo> medicationInfos = notTakenMedications.stream()
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
