package server.yakssok.domain.medication.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationGroupedResponse;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationService {
	private final MedicationRepository medicationRepository;
	private final MedicationIntakeDayRepository medicationIntakeDayRepository;
	private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;

	@Transactional(readOnly = true)
	public MedicationGroupedResponse findMedications(Long userId) {
		List<Medication> medications = medicationRepository.findAllUserMedications(userId);
		List<MedicationCardResponse> medicationCardResponses = medications.stream().map(
			medication -> MedicationCardResponse.from(medication)
		).toList();
		return MedicationGroupedResponse.of(medicationCardResponses);
	}

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequest request) {
		Medication medication = saveMedication(request, userId);
		saveMedicationTimes(request, medication);
		saveMedicationDays(request, medication);
	}

	private void saveMedicationTimes(CreateMedicationRequest request, Medication medication) {
		List<MedicationIntakeTime> medicationsTimes = request.toMedicationsTimes(medication);
		medicationIntakeTimeRepository.saveAll(medicationsTimes);
	}

	private void saveMedicationDays(CreateMedicationRequest request, Medication medication) {
		List<MedicationIntakeDay> intakeDays = request.toIntakeDays(medication);
		medicationIntakeDayRepository.saveAll(intakeDays);
	}

	private Medication saveMedication(CreateMedicationRequest request, Long userId) {
		Medication medication = request.toMedication(userId);
		medicationRepository.save(medication);
		return medication;
	}

	@Transactional
	public void endMedication(Long medicationId) {
		Medication medication = getMedication(medicationId);
		LocalDateTime currentDateTime = LocalDateTime.now();
		medication.end(currentDateTime);
		deleteTodayUpcomingSchedules(medicationId, currentDateTime.toLocalDate(), currentDateTime.toLocalTime());
	}

	private Medication getMedication(Long medicationId) {
		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new MedicationException(ErrorCode.NOT_FOUND_MEDICATION));
		return medication;
	}

	private void deleteTodayUpcomingSchedules(Long medicationId, LocalDate currentDate, LocalTime currentTime) {
		medicationScheduleRepository.deleteTodayUpcomingSchedules(medicationId, currentDate, currentTime);
	}
}
