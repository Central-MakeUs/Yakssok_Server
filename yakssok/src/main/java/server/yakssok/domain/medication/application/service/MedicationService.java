package server.yakssok.domain.medication.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationStatus;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeDayRepository;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeTimeRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationGroupedResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationProgressResponse;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationService {
	private final MedicationRepository medicationRepository;
	private final MedicationIntakeDayRepository medicationIntakeDayRepository;
	private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	private final MedicationScheduleService medicationScheduleService;

	@Transactional(readOnly = true)
	public MedicationGroupedResponse findMedications(Long userId, String statusParam) {
		LocalDateTime now = LocalDateTime.now();
		MedicationStatus medicationStatus = toMedicationStatus(statusParam);
		List<Medication> medications = (medicationStatus == null)
			? medicationRepository.findAllUserMedications(userId)
			: findByStatus(userId, medicationStatus, now);
		return mapToGroupedResponse(medications);
	}

	private MedicationGroupedResponse mapToGroupedResponse(List<Medication> meds) {
		List<MedicationCardResponse> medicationCardResponses = meds.stream()
			.map(MedicationCardResponse::from)
			.toList();
		return MedicationGroupedResponse.of(medicationCardResponses);
	}

	private MedicationStatus toMedicationStatus(String statusParam) {
		if (!StringUtils.hasText(statusParam)) {
			return null;
		}
		return MedicationStatus.from(statusParam);
	}

	private List<Medication> findByStatus(Long userId, MedicationStatus status, LocalDateTime now) {
		return switch (status) {
			case PLANNED -> medicationRepository.findUserPlannedMedications(userId, now);
			case TAKING -> medicationRepository.findUserTakingMedications(userId, now);
			case ENDED -> medicationRepository.findUserEndedMedications(userId, now);
		};
	}

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequest request) {
		Medication medication = saveMedication(request, userId);
		saveMedicationTimes(request, medication);
		saveMedicationDays(request, medication);

		if (isTodayStart(medication, request.intakeDays())) {
			medicationScheduleService.createTodaySchedules(medication, request.intakeTimes());
		}
	}

	private static boolean isTodayStart(Medication medication, List<DayOfWeek> intakeDays) {
		LocalDate today = LocalDate.now();
		boolean isTodayStartDate = today.equals(medication.getStartDate());
		boolean isTodayMedicationDay = intakeDays.stream()
			.anyMatch(day -> day == today.getDayOfWeek());
		return isTodayStartDate && isTodayMedicationDay;
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
		medicationScheduleService.deleteTodayUpcomingSchedules(medicationId, currentDateTime);
	}

	private Medication getMedication(Long medicationId) {
		Medication medication = medicationRepository.findById(medicationId)
			.orElseThrow(() -> new MedicationException(ErrorCode.NOT_FOUND_MEDICATION));
		return medication;
	}
}
