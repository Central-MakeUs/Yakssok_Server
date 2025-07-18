package server.yakssok.domain.medication.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import lombok.Getter;
import server.yakssok.domain.medication.application.exception.MedicationException;

@Getter
public enum MedicationStatus {
	PLANNED(),
	TAKING(),
	ENDED();

	public static MedicationStatus from(String status) {
		try {
			return MedicationStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new MedicationException(INVALID_INPUT_VALUE);
		}
	}
}
