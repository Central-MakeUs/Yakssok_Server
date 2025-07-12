package server.yakssok.domain.medication.application.service;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;


import lombok.extern.slf4j.Slf4j;
import server.yakssok.domain.medication.batch.MedicationScheduleJob;
import server.yakssok.domain.medication.domain.entity.AlarmSound;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.user.domain.entity.User;
import server.yakssok.domain.user.domain.repository.UserRepository;

@SpringBootTest
@Slf4j
@Transactional
public class MedicationScheduleBatchTest {

	@Autowired
	private MedicationScheduleJob medicationScheduleJob;

	@Autowired
	private MedicationRepository medicationRepository;

	@Autowired
	private MedicationScheduleRepository medicationScheduleRepository;

	@Autowired
	private UserRepository userRepository;

	@Test
	void 배치_스케줄_생성_성능_테스트() {
		User user = createTestUser();

		LocalDate today = LocalDate.now();
		DayOfWeek todayDayOfWeek = today.getDayOfWeek();

		List<Medication> medications = createMedicationList(user, today.minusDays(1), today.plusDays(1), todayDayOfWeek, 30000);
		medicationRepository.saveAll(medications);

		long start = System.currentTimeMillis();
		medicationScheduleJob.runToday();
		long end = System.currentTimeMillis();

		log.info("배치 작업 소요 시간: {} ms", (end - start));
		log.info("생성된 스케줄 수: {}", medicationScheduleRepository.count());
	}

	private User createTestUser() {
		return userRepository.save(User.create(
			"테스트 유저", null, "kakao", "provider123", true, "testToken"
		));
	}

	private List<Medication> createMedicationList(User user, LocalDate startDate, LocalDate endDate, DayOfWeek dayOfWeek, int count) {
		return IntStream.range(0, count)
			.mapToObj(i -> {
				Medication medication = Medication.create(
					"약-" + i,
					startDate,
					endDate,
					AlarmSound.YAKSSUK,
					MedicationType.CHRONIC,
					user.getId(),
					1
				);
				MedicationIntakeDay.of(dayOfWeek, medication);
				MedicationIntakeTime.create(LocalTime.of(8, 0), medication);
				return medication;
			})
			.toList();
	}
}
