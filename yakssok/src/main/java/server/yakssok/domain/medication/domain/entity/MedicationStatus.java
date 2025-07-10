package server.yakssok.domain.medication.domain.entity;

import lombok.Getter;

@Getter
public enum MedicationStatus {
	PLANNED(1),
	TAKING(2),
	COMPLETED(3);

	private final int priority;

	MedicationStatus(int priority) {
		this.priority = priority;
	}
}
