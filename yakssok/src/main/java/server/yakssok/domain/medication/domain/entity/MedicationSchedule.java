package server.yakssok.domain.medication.domain.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MedicationSchedule {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String medicineName;

	private LocalDate scheduledDate;
	private LocalTime scheduledTime;
	private boolean isTaken;
	private Long medicationId;

	public static MedicationSchedule create(String medicineName, LocalDate scheduledDate, LocalTime scheduledTime, Long medicationId) {
		return new MedicationSchedule(null, medicineName, scheduledDate, scheduledTime, false, medicationId);
	}
}
