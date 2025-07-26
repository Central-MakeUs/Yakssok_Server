package server.yakssok.domain.medication.presentation.dto.response;

public record MedicationProgressResponse(
	boolean isProgress
) {
	public static MedicationProgressResponse of(boolean isProgress) {
		return new MedicationProgressResponse(isProgress);
	}
}
