package server.yakssok.domain.medication.domain.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.user.domain.entity.User;

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

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MedicationStatus medicationStatus;

	@OneToMany(mappedBy = "medication", cascade = CascadeType.PERSIST)
	private List<MedicationIntakeTime> intakeTimes = new ArrayList<>();

	@OneToMany(mappedBy = "medication", cascade = CascadeType.PERSIST)
	private List<MedicationIntakeDay> intakeDays = new ArrayList<>();

	private int intakeCount;

	private Medication(String medicineName, LocalDate startDate, LocalDate endDate, AlarmSound alarmSound,
		MedicationType medicationType, User user, int intakeCount) {
		this.medicineName = medicineName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.alarmSound = alarmSound;
		this.medicationType = medicationType;
		this.user = user;
		this.medicationStatus = calculateStatus(startDate, endDate);
		this.intakeCount = intakeCount;
	}

	public static Medication create(String medicineName, LocalDate startDate, LocalDate endDate,
		AlarmSound alarmSound, MedicationType medicationType, User user, int intakeCount) {
		return new Medication(
			medicineName,
			startDate,
			endDate,
			alarmSound,
			medicationType,
			user,
			intakeCount
		);
	}

	public void updateStatus() {
		this.medicationStatus = calculateStatus(this.startDate, this.endDate);
	}

	private MedicationStatus calculateStatus(LocalDate startDate, LocalDate endDate) {
		LocalDate today = LocalDate.now();
		if (today.isBefore(startDate)) {
			return MedicationStatus.PLANNED;
		} else if (!today.isAfter(endDate)) {
			return MedicationStatus.TAKING;
		} else {
			return MedicationStatus.COMPLETED;
		}
	}
}
