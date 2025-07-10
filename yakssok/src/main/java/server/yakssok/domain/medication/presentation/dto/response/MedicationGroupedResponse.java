package server.yakssok.domain.medication.presentation.dto.response;

import java.util.List;
import java.util.Map;

public record MedicationGroupedResponse(
	Map<String, List<MedicationCardResponse>> medications
) {

	public static MedicationGroupedResponse of(Map<String, List<MedicationCardResponse>> medications) {
		return new MedicationGroupedResponse(medications);
	}
}
