package server.yakssok.domain.medication.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationType;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

class MedicationScheduleGeneratorTest {

	private final MedicationScheduleGenerator generator = new MedicationScheduleGenerator();

	private Medication medication(LocalDate start, LocalDate end) {
		Medication m = mock(Medication.class);
		when(m.getStartDate()).thenReturn(start);
		when(m.getEndDate()).thenReturn(end);
		lenient().when(m.getId()).thenReturn(1L);
		lenient().when(m.getUserId()).thenReturn(10L);
		lenient().when(m.getMedicineName()).thenReturn("타이레놀");
		lenient().when(m.getMedicationType()).thenReturn(MedicationType.CHRONIC);
		return m;
	}

	private MedicationIntakeDay intakeDay(DayOfWeek day) {
		MedicationIntakeDay d = mock(MedicationIntakeDay.class);
		when(d.getDayOfWeek()).thenReturn(day);
		return d;
	}

	@Nested
	@DisplayName("generateSchedulesAfter")
	class GenerateSchedulesAfter {

		@Test
		@DisplayName("오늘 날짜에서 cutoff 시간 이전 스케줄은 제외된다")
		void filters_times_before_cutoff_on_today() {
			// 2025-08-11(월), cutoff = 12:00
			LocalDate today = LocalDate.of(2025, 8, 11);
			LocalDateTime cutoff = LocalDateTime.of(today, LocalTime.of(12, 0));
			Medication m = medication(LocalDate.of(2025, 8, 1), today);

			List<MedicationSchedule> result = generator.generateSchedulesAfter(
				m,
				List.of(LocalTime.of(9, 0), LocalTime.of(14, 0)),  // 9시 제외, 14시 포함
				List.of(DayOfWeek.MONDAY),
				cutoff
			);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).getScheduledTime()).isEqualTo(LocalTime.of(14, 0));
		}

		@Test
		@DisplayName("내일 이후 날짜는 cutoff 시간 필터 없이 해당 요일 전부 포함된다")
		void includes_all_times_for_future_dates() {
			// cutoff = 2025-08-11(월) 23:00, 다음 월 = 2025-08-18
			LocalDate today = LocalDate.of(2025, 8, 11);
			LocalDateTime cutoff = LocalDateTime.of(today, LocalTime.of(23, 0));
			Medication m = medication(LocalDate.of(2025, 8, 1), LocalDate.of(2025, 8, 18));

			List<MedicationSchedule> result = generator.generateSchedulesAfter(
				m,
				List.of(LocalTime.of(9, 0), LocalTime.of(14, 0)),
				List.of(DayOfWeek.MONDAY),
				cutoff
			);

			// 오늘(08-11): 23:00 이후 없음 → 0개 / 08-18(월): 2개
			assertThat(result).hasSize(2);
			assertThat(result).allMatch(s -> s.getScheduledDate().isEqual(LocalDate.of(2025, 8, 18)));
		}

		@Test
		@DisplayName("intakeDays에 없는 요일은 스케줄이 생성되지 않는다")
		void filters_by_day_of_week() {
			// 2025-08-11(월)~17(일), 수요일만
			LocalDateTime cutoff = LocalDateTime.of(2025, 8, 10, 0, 0);
			Medication m = medication(LocalDate.of(2025, 8, 11), LocalDate.of(2025, 8, 17));

			List<MedicationSchedule> result = generator.generateSchedulesAfter(
				m, List.of(LocalTime.of(9, 0)), List.of(DayOfWeek.WEDNESDAY), cutoff);

			// 수요일: 2025-08-13 1개
			assertThat(result).hasSize(1);
			assertThat(result.get(0).getScheduledDate()).isEqualTo(LocalDate.of(2025, 8, 13));
		}

		@Test
		@DisplayName("생성된 스케줄에 약 이름과 유형이 스냅샷으로 저장된다")
		void snapshot_medicineName_and_type() {
			LocalDate today = LocalDate.of(2025, 8, 13); // WEDNESDAY
			LocalDateTime cutoff = LocalDateTime.of(today, LocalTime.of(8, 0));
			Medication m = medication(today, today);

			List<MedicationSchedule> result = generator.generateSchedulesAfter(
				m, List.of(LocalTime.of(9, 0)), List.of(DayOfWeek.WEDNESDAY), cutoff);

			assertThat(result).hasSize(1);
			assertThat(result.get(0).getMedicineName()).isEqualTo("타이레놀");
			assertThat(result.get(0).getMedicationType()).isEqualTo(MedicationType.CHRONIC);
		}
	}

	@Nested
	@DisplayName("generateAllSchedules")
	class GenerateAllSchedules {

		@Test
		@DisplayName("날짜 범위 × 요일 × 복용시간 수만큼 스케줄이 생성된다")
		void generates_correct_count() {
			// 2025-08-11(월) ~ 2025-08-17(일), 월·수·금, 2회/일 → 6개
			Medication m = medication(LocalDate.of(2025, 8, 11), LocalDate.of(2025, 8, 17));

			MedicationIntakeDay mon = intakeDay(DayOfWeek.MONDAY);
			MedicationIntakeDay wed = intakeDay(DayOfWeek.WEDNESDAY);
			MedicationIntakeDay fri = intakeDay(DayOfWeek.FRIDAY);
			when(m.getIntakeDays()).thenReturn(List.of(mon, wed, fri));

			List<MedicationSchedule> result = generator.generateAllSchedules(
				m, List.of(LocalTime.of(9, 0), LocalTime.of(14, 0)));

			assertThat(result).hasSize(6);
		}
	}
}
