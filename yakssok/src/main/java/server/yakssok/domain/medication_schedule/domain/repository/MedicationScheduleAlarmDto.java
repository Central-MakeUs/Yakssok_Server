package server.yakssok.domain.medication_schedule.domain.repository;

public record MedicationScheduleAlarmDto(
	Long scheduleId,
	String medicineName,
	Long userId,
	String userNickName
) {
}
