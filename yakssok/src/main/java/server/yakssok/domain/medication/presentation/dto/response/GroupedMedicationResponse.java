package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;


public record GroupedMedicationResponse(

	String type,
	String label,
	List<MedicationCardResponse> medications
) {}