package server.yakssok.domain.medication.domain.entity;

import lombok.Getter;

@Getter
public enum MedicationStatus {
	PLANNED,
	TAKING,
	COMPLETED;
}
