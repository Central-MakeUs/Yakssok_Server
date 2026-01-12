package server.yakssok.domain.medication.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeDayRepository;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeTimeRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequestV2;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationServiceV2 {
	private final MedicationRepository medicationRepository;
	private final MedicationIntakeDayRepository medicationIntakeDayRepository;
	private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	private final MedicationScheduleService medicationScheduleService;

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequestV2 request) {
		Medication medication = saveMedication(request, userId);
		saveMedicationTimes(request, medication);
		saveMedicationDays(request, medication);
		medicationScheduleService.createAllSchedules(medication, request.intakeTimes());
	}

	private void saveMedicationTimes(CreateMedicationRequestV2 request, Medication medication) {
		List<MedicationIntakeTime> medicationsTimes = request.toMedicationsTimes(medication);
		medicationIntakeTimeRepository.saveAll(medicationsTimes);
	}

	private void saveMedicationDays(CreateMedicationRequestV2 request, Medication medication) {
		List<MedicationIntakeDay> intakeDays = request.toIntakeDays(medication);
		medicationIntakeDayRepository.saveAll(intakeDays);
	}

	private Medication saveMedication(CreateMedicationRequestV2 request, Long userId) {
		Medication medication = request.toMedication(userId);
		medicationRepository.save(medication);
		return medication;
	}

	@Transactional
	public void endMedication(Long medicationId) {
		Medication medication = getMedication(medicationId);
		LocalDateTime currentDateTime = LocalDateTime.now();
		medication.end(currentDateTime);
		medicationScheduleService.deleteAllUpcomingSchedules(medicationId, currentDateTime);
	}

	private Medication getMedication(Long medicationId) {
		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new MedicationException(ErrorCode.NOT_FOUND_MEDICATION));
		return medication;
	}
}
