package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;

public record MedicationGroupedResponse(
	List<MedicationCardResponse> medicationCardResponses
) {

	public static MedicationGroupedResponse of(List<MedicationCardResponse> medicationCardResponses) {
		return new MedicationGroupedResponse(medicationCardResponses);
	}
}
