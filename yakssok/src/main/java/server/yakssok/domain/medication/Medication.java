package server.yakssok.domain.medication;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import server.yakssok.domain.user.User;

@Entity
public class Medication {
	private String medicineName;
	private Cycle cycle;
	private LocalTime alarmTime;

	private LocalDate startDate;
	private LocalDate endDate;

	@Enumerated(EnumType.STRING)
	private AlarmSound alarmSound;
	@Enumerated(EnumType.STRING)
	private MedicationType medicationType;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
}
