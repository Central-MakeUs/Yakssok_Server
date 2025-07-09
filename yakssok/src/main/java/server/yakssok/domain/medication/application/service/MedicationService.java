package server.yakssok.domain.medication.application.service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
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
		List<Medication> medications = medicationRepository.findAllUserMedications(userId); //TODO : fetch join

		System.out.println("=========================1===========================");
		//정렬
		Comparator<Medication> comparator = Comparator
			.comparing((Medication m) -> m.getMedicationType().getPriority())
			.thenComparing(m -> m.getMedicationStatus().getPriority())
			.thenComparing(Medication::getId, Comparator.reverseOrder());
		medications.sort(comparator);

		System.out.println("==========================2==========================");
		//map으로 반환
		Map<String, List<MedicationCardResponse>> group = Arrays.stream(MedicationType.values())
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
		return MedicationGroupedResponse.of(group);
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
