package server.yakssok.domain.medication_schedule.application.service;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication_schedule.application.exception.MedicationScheduleException;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.global.exception.ErrorCode;
@RequiredArgsConstructor
@Component
public class MedicationScheduleValidator {
	private final MedicationRepository medicationRepository;

	public void validateOwnership(Long userId, MedicationSchedule schedule) {
		Long medicationId = schedule.getMedicationId();
		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new MedicationScheduleException(ErrorCode.NOT_FOUND_MEDICATION));
		if (!medication.getUserId().equals(userId)) {
			throw new MedicationScheduleException(ErrorCode.FORBIDDEN);
		}
	}

	public void validateTodaySchedule(MedicationSchedule schedule) {
		if (!schedule.isTodaySchedule()) {
			throw new MedicationScheduleException(ErrorCode.NOT_TODAY_SCHEDULE);
		}
	}
}
