package server.yakssok.domain.medication_schedule.presentation.dto;

public record TodayMedicationScheduleResponse(
	String date,
	Long scheduleId,
	String medicationType,
	String medicationName,
	String intakeTime,
	boolean isTaken
) {
	public static TodayMedicationScheduleResponse from(
		String date,
		Long scheduleId,
		String medicationType,
		String medicationName,
		String intakeTime,
		boolean isTaken
	) {
		return new TodayMedicationScheduleResponse(date, scheduleId, medicationType, medicationName, intakeTime, isTaken);
	}
}
