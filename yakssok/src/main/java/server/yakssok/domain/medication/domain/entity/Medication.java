package server.yakssok.domain.medication.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Medication {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String medicineName;

	@Column(nullable = false)
	private LocalDate startDate;
	private LocalDate endDate;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private AlarmSound alarmSound;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MedicationType medicationType;

	private Long userId;

	@OneToMany(mappedBy = "medication", cascade = CascadeType.PERSIST)
	private List<MedicationIntakeTime> intakeTimes = new ArrayList<>();

	@OneToMany(mappedBy = "medication", cascade = CascadeType.PERSIST)
	private List<MedicationIntakeDay> intakeDays = new ArrayList<>();

	private int intakeCount;

	private Medication(
		String medicineName,
		LocalDate startDate,
		LocalDate endDate,
		AlarmSound alarmSound,
		MedicationType medicationType,
		Long userId,
		int intakeCount
	) {
		this.medicineName = medicineName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alarmSound = alarmSound;
		this.medicationType = medicationType;
		this.userId = userId;
		this.intakeCount = intakeCount;
	}

	public static Medication create(
		String medicineName,
		LocalDate startDate,
		LocalDate endDate,
		AlarmSound alarmSound,
		MedicationType medicationType,
		Long userId,
		int intakeCount
	) {
		return new Medication(
			medicineName,
			startDate,
			endDate,
			alarmSound,
			medicationType,
			userId,
			intakeCount
		);
	}

	private MedicationStatus calculateStatus(LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();
		if (today.isBefore(startDate)) {
			return MedicationStatus.PLANNED;
		} else if (isNotExistEndDate() || !today.isAfter(endDate)) {
			return MedicationStatus.TAKING;
		} else {
			return MedicationStatus.COMPLETED;
		}
	}

	private boolean isNotExistEndDate() {
		return this.endDate == null;
	}

	public MedicationStatus getMedicationStatus() {
		return calculateStatus(startDate, endDate);
	}

	public void changeEndDate(LocalDate date) {
		this.endDate = date;
	}
}
