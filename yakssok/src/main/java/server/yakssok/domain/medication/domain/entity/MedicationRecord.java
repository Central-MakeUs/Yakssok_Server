package server.yakssok.domain.medication.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class MedicationRecord {
	private LocalDate date;
	private LocalTime time;
	private boolean isTaken;

	@JoinColumn(name = "medication_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Medication medication;
}
