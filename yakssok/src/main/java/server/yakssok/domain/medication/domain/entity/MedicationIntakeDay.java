package server.yakssok.domain.medication.domain.entity;

import java.time.DayOfWeek;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class MedicationIntakeDay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private DayOfWeek dayOfWeek;

	@ManyToOne(fetch = FetchType.LAZY)
	private Medication medication;

	private MedicationIntakeDay(DayOfWeek dayOfWeek, Medication medication) {
		this.dayOfWeek = dayOfWeek;
		this.medication = medication;
	}

	public static MedicationIntakeDay of(DayOfWeek day, Medication medication) {
		return new MedicationIntakeDay(day, medication);
	}
}
