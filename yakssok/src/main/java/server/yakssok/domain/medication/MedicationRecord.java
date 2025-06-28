package server.yakssok.domain.medication;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class MedicationRecord {
	//날짜
	private LocalDate date;
	//시간
	private LocalTime time;
	//복용 여부
	private boolean isTaken;

	@JoinColumn(name = "medication_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private Medication medication;
}
