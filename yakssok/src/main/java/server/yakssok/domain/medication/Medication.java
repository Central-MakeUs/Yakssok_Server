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
	//약 이름
	private String medicineName;
	//알람 주기
	private Cycle cycle;
	//알람 시간
	private LocalTime alarmTime;

	//시작일 종료일
	private LocalDate startDate;
	private LocalDate endDate;

	//알람음
	@Enumerated(EnumType.STRING)
	private AlarmSound alarmSound;
	//약 종류
	@Enumerated(EnumType.STRING)
	private MedicationType medicationType;

	@JoinColumn(name = "user_id")
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
}
