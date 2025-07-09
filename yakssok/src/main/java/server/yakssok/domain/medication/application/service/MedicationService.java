package server.yakssok.domain.medication.application.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.domain.medication.presentation.dto.response.MedicationGroupedResponse;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;


@Service
@RequiredArgsConstructor
public class MedicationService {
	private final MedicationRepository medicationRepository;
	private final MedicationIntakeDayRepository medicationIntakeDayRepository;
	private final MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public MedicationGroupedResponse findMedications(Long userId) {
		List<Medication> medications = medicationRepository.findAllUserMedications(userId);

		Comparator<Medication> comparator = getMedicationComparator();
		medications.sort(comparator);

		Map<String, List<MedicationCardResponse>> group = groupByMedicationType(medications, comparator); //TODO: n+1 문제 해결
		return MedicationGroupedResponse.of(group);
	}

	/**
	 * 복약 타입에 따라 정렬 기준을 설정합니다.
	 * 1. MedicationType의 우선순위에 따라 정렬
	 * 2. MedicationStatus의 우선순위에 따라 정렬
	 * 3. ID를 기준으로 내림차순 정렬
	 */
	private Comparator<Medication> getMedicationComparator() {
		return Comparator
			.comparing((Medication m) -> m.getMedicationType().getPriority())
			.thenComparing(m -> m.getMedicationStatus().getPriority())
			.thenComparing(Medication::getId, Comparator.reverseOrder());
	}

	private Map<String, List<MedicationCardResponse>> groupByMedicationType(List<Medication> medications, Comparator<Medication> comparator) {
		return Arrays.stream(MedicationType.values())
			.map(Enum::name)
			.collect(Collectors.toMap(
				typeName -> typeName,
				typeName -> medications.stream()
					.filter(med -> med.getMedicationType().name().equals(typeName))
					.sorted(comparator)
					.map(MedicationCardResponse::from)
					.toList(),
				(a, b) -> a,
				LinkedHashMap::new
			));
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
