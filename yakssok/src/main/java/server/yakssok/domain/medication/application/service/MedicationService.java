package server.yakssok.domain.medication.application.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeDayRepository;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeTimeRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.GroupedMedicationResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.common.reponse.PageResponse;


@Service
@RequiredArgsConstructor
public class MedicationService {
	private final MedicationRepository medicationRepository;
	private final MedicationIntakeDayRepository medicationIntakeDayRepository;
	private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public PageResponse<MedicationCardResponse> findMedications(Long userId) {
		List<Medication> medications = medicationRepository.findAllUserMedications(userId); //TODO : fetch join

		//정렬
		Comparator<Medication> comparator = Comparator
			.comparing((Medication m) -> m.getMedicationType().getPriority()) // 1. 타입 순
			.thenComparing(m -> m.getMedicationStatus().getPriority())     // 2. 상태 우선순위
			.thenComparing(Medication::getId, Comparator.reverseOrder());  // 3. 최신순

		medications.sort(comparator);
		//medications을 카테고리, 복약 상태, 최신순으로 정렬
		return null;

	}

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequest request) {
		User user = userService.getUser(userId);
		Medication medication = saveMedication(request, user);
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

	private Medication saveMedication(CreateMedicationRequest request, User user) {
		Medication medication = request.toMedication(user);
		medicationRepository.save(medication);
		return medication;
	}
}
