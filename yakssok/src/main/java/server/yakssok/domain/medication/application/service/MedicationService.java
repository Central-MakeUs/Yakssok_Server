package server.yakssok.domain.medication.application.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.FindMedicationResponseCard;
import server.yakssok.domain.user.application.service.UserService;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.global.common.reponse.PageResponse;

@Service
@RequiredArgsConstructor
public class MedicationService {
	private final MedicationRepository medicationRepository;
	private final UserService userService;

	@Transactional(readOnly = true)
	public PageResponse<FindMedicationResponseCard> findMedications(Long userId) {
		List<Medication> allUserMedications = medicationRepository.findAllUserMedications(userId);
		List<FindMedicationResponseCard> medicationResponseCards = allUserMedications.stream()
			.map(medication -> FindMedicationResponseCard.of(medication)).toList();
		return PageResponse.of(medicationResponseCards);
	}

	@Transactional
	public void createMedication(Long userId, CreateMedicationRequest createMedicationRequest) {
		User user = userService.getUser(userId);
		Medication medication = createMedicationRequest.toMedication(user);
		List<MedicationIntakeTime> medicationsTimes = createMedicationRequest.toMedicationsTimes(medication);
		for (MedicationIntakeTime medicationsTime : medicationsTimes) {
			medication.addIntakeTime(medicationsTime);
		}
		medicationRepository.save(medication);
	}
}
