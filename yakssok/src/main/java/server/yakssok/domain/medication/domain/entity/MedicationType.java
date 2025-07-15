package server.yakssok.domain.medication.domain.entity;

import static server.yakssok.global.exception.ErrorCode.*;

import lombok.Getter;
import server.yakssok.domain.medication.application.exception.MedicationException;
@Getter
public enum MedicationType {
	CHRONIC,
	MENTAL,
	SUPPLEMENT,
	BEAUTY,
	HIGHRISK,
	DIET,
	TEMPORARY,
	OTHER;

	public static MedicationType from(String name) {
		try {
			return MedicationType.valueOf(name.toUpperCase());
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new MedicationException(INVALID_INPUT_VALUE);
		}
	}
}
