package server.yakssok.domain.medication_schedule.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.yakssok.domain.BaseEntity;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication_schedule.domain.policy.OverduePolicy;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicationSchedule extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private LocalDate scheduledDate;
	private LocalTime scheduledTime;
	private boolean isTaken;
	private Long medicationId;
	private Long userId;
	private String medicineName;

	@Enumerated(EnumType.STRING)
	private MedicationType medicationType;

	public static MedicationSchedule create(LocalDate scheduledDate, LocalTime scheduledTime,
		Long medicationId, Long userId, String medicineName, MedicationType medicationType) {
		return new MedicationSchedule(
			scheduledDate, scheduledTime, false, medicationId, userId, medicineName, medicationType
		);
	}

	private MedicationSchedule(
		LocalDate scheduledDate,
		LocalTime scheduledTime,
		boolean isTaken,
		Long medicationId,
		Long userId,
		String medicineName,
		MedicationType medicationType
	) {
		this.scheduledDate = scheduledDate;
		this.scheduledTime = scheduledTime;
		this.isTaken = isTaken;
		this.medicationId = medicationId;
		this.userId = userId;
		this.medicineName = medicineName;
		this.medicationType = medicationType;
	}

	public boolean isTodaySchedule() {
		return scheduledDate.equals(LocalDate.now());
	}

	public void switchTake() {
		this.isTaken = !this.isTaken;
	}

	public LocalDateTime scheduledDateTime() {
		return LocalDateTime.of(scheduledDate, scheduledTime);
	}

	/** 마지막 잔소리 이후에 해당하는 지연인지 */
	public boolean isOverdueAfterNag(LocalDateTime nagBoundary, OverduePolicy policy) {
		return scheduledDateTime().plusMinutes(policy.graceMinutes()).isAfter(nagBoundary);
	}
}
