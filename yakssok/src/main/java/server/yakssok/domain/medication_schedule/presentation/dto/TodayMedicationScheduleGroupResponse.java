package server.yakssok.domain.medication_schedule.presentation.dto;

import java.util.List;

public record TodayMedicationScheduleGroupResponse(
	List<TodayMedicationScheduleResponse> todayMedicationScheduleResponses
) {
	public static TodayMedicationScheduleGroupResponse of(
		List<TodayMedicationScheduleResponse> todayMedicationScheduleResponses) {
		return new TodayMedicationScheduleGroupResponse(todayMedicationScheduleResponses);
	}
}
