package server.yakssok.domain.medication.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.Valid;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.FindMedicationResponse;

@Service
public class MedicationService {

	@Transactional(readOnly = true)
	public FindMedicationResponse findMedications(Long userId) {
		return null;
	}

	@Transactional
	public void createMedication(Long userId, @Valid CreateMedicationRequest createMedicationRequest) {

	}
}
