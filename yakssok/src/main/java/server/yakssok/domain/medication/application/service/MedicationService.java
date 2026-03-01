package server.yakssok.domain.medication.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationStatus;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication.domain.entity.SoundType;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeDayRepository;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeTimeRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.request.UpdateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationGroupedResponse;
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
		MedicationStatus status = toMedicationStatus(statusParam);
		List<Medication> medications = (status == null)
			? medicationRepository.findAllUserMedications(userId)
			: findByStatus(userId, status, now);
		return toGroupedResponse(medications);
	}

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequest request) {
		Medication medication = medicationRepository.save(request.toMedication(userId));
		medicationIntakeDayRepository.saveAll(request.toIntakeDays(medication));
		medicationIntakeTimeRepository.saveAll(request.toMedicationsTimes(medication));
		medicationScheduleService.createAllSchedules(medication, request.intakeTimes());
	}

	@Transactional
	public void updateMedication(Long userId, Long medicationId, UpdateMedicationRequest request) {
		Medication medication = getMedication(medicationId);
		validateOwnership(userId, medication);

		LocalDateTime cutoff = resolveCutoff(request.startDate());
		deleteUpcomingSchedulesAndIntakes(medicationId, cutoff);

		medication.update(
			request.name(), request.startDate(), request.endDate(),
			SoundType.from(request.alarmSound()),
			MedicationType.from(request.medicineType()),
			request.intakeCount()
		);

		medicationIntakeDayRepository.saveAll(request.toIntakeDays(medication));
		medicationIntakeTimeRepository.saveAll(request.toMedicationsTimes(medication));
		medicationScheduleService.createSchedulesAfter(
			medication, request.intakeTimes(), request.intakeDays(), cutoff);
	}

	@Transactional
	public void endMedication(Long medicationId) {
		Medication medication = getMedication(medicationId);
		LocalDateTime now = LocalDateTime.now();
		medication.end(now);
		medicationScheduleService.deleteAllUpcomingSchedules(medicationId, now);
	}

	@Transactional
	public void deleteAllByUserId(Long userId) {
		List<Medication> medications = medicationRepository.findAllUserMedications(userId);
		List<Long> medicationIds = medications.stream().map(Medication::getId).toList();
		medicationIntakeDayRepository.deleteAllByMedicationIds(medicationIds);
		medicationIntakeTimeRepository.deleteAllByMedicationIds(medicationIds);
		medicationRepository.deleteAll(medications);
		medicationScheduleService.deleteAllByMedicationIds(medicationIds);
	}

	// 공유 헬퍼
	private Medication getMedication(Long medicationId) {
		return medicationRepository.findById(medicationId)
			.orElseThrow(() -> new MedicationException(ErrorCode.NOT_FOUND_MEDICATION));
	}

	private void validateOwnership(Long userId, Medication medication) {
		if (!medication.getUserId().equals(userId)) {
			throw new MedicationException(ErrorCode.FORBIDDEN);
		}
	}

	// updateMedication 헬퍼
	private LocalDateTime resolveCutoff(LocalDate startDate) {
		LocalDateTime now = LocalDateTime.now();
		return startDate.isAfter(now.toLocalDate()) ? startDate.atStartOfDay() : now;
	}

	private void deleteUpcomingSchedulesAndIntakes(Long medicationId, LocalDateTime cutoff) {
		medicationScheduleService.deleteAllUpcomingSchedules(medicationId, cutoff);
		medicationIntakeDayRepository.deleteAllByMedicationIds(List.of(medicationId));
		medicationIntakeTimeRepository.deleteAllByMedicationIds(List.of(medicationId));
	}

	// findMedications 헬퍼
	private MedicationGroupedResponse toGroupedResponse(List<Medication> medications) {
		return MedicationGroupedResponse.of(
			medications.stream().map(MedicationCardResponse::from).toList()
		);
	}

	private MedicationStatus toMedicationStatus(String statusParam) {
		if (!StringUtils.hasText(statusParam)) return null;
		return MedicationStatus.from(statusParam);
	}

	private List<Medication> findByStatus(Long userId, MedicationStatus status, LocalDateTime now) {
		return switch (status) {
			case PLANNED -> medicationRepository.findUserPlannedMedications(userId, now);
			case TAKING -> medicationRepository.findUserTakingMedications(userId, now);
			case ENDED -> medicationRepository.findUserEndedMedications(userId, now);
		};
	}
}
