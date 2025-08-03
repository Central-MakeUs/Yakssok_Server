package server.yakssok.domain.medication_schedule.domain.repository;

import server.yakssok.domain.medication.domain.entity.SoundType;

public record MedicationScheduleAlarmDto(
	Long scheduleId,
	String medicineName,
	Long userId,
	String userNickName,
	SoundType soundType
) {
}
