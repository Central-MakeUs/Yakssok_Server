package server.yakssok.domain.medication.domain.entity;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationIntakeTime {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalTime time;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "medication_id")
	private Medication medication;

	public static MedicationIntakeTime create(LocalTime time, Medication medication) {
		return new MedicationIntakeTime(time, medication);
	}

	public MedicationIntakeTime(LocalTime time, Medication medication) {
		this.time = time;
		this.medication = medication;
	}

	void assignMedication(Medication medication) {
		this.medication = medication;
	}
}
