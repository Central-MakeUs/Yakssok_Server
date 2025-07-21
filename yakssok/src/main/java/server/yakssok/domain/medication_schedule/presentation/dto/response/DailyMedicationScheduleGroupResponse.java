package server.yakssok.domain.medication_schedule.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;


public record DailyMedicationScheduleGroupResponse(
	LocalDate date,
	boolean allTaken,
	List<MedicationScheduleResponse> schedules
) {
	public static DailyMedicationScheduleGroupResponse of(
		LocalDate date,
		List<MedicationScheduleResponse> schedules
	) {
		boolean allTaken = !schedules.isEmpty() && schedules.stream().allMatch(MedicationScheduleResponse::isTaken);
		return new DailyMedicationScheduleGroupResponse(date, allTaken, schedules);
	}
}