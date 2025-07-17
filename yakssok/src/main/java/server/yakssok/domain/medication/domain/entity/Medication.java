package server.yakssok.domain.medication.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationUtils;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Medication {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String medicineName;

	@Column(nullable = false)
	private LocalDate startDate;
	private LocalDateTime endDateTime;

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
		LocalDateTime endDateTime,
		AlarmSound alarmSound,
		MedicationType medicationType,
		Long userId,
		int intakeCount
	) {
		this.medicineName = medicineName;
		this.startDate = startDate;
		this.endDateTime = endDateTime;
		this.alarmSound = alarmSound;
		this.medicationType = medicationType;
		this.userId = userId;
		this.intakeCount = intakeCount;
	}

	private static LocalDateTime convertToEndDateTime(LocalDate endDate) {
		return endDate == null ? null : MedicationUtils.toEndOfDay(endDate);
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
			convertToEndDateTime(endDate),
			alarmSound,
			medicationType,
			userId,
			intakeCount
		);
	}

	private MedicationStatus calculateStatus(LocalDate startDate, LocalDateTime endDateTime) {
		LocalDateTime now = LocalDateTime.now();
		if (now.toLocalDate().isBefore(startDate)) {
			return MedicationStatus.PLANNED;
		}
		if (isNotExistEndDate() || !now.isAfter(endDateTime)) {
			return MedicationStatus.TAKING;
		}
		return MedicationStatus.COMPLETED;
	}

	private boolean isNotExistEndDate() {
		return this.endDateTime == null;
	}

	public MedicationStatus getMedicationStatus() {
		return calculateStatus(startDate, endDateTime);
	}

	public void end(LocalDateTime endDateTime) {
		this.endDateTime = endDateTime;
	}

	public LocalDate getEndDate() {
		return endDateTime == null ? null : endDateTime.toLocalDate();
	}
}
