package server.yakssok.domain.medication.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class MedicationDomainTest {

	@Test
	@DisplayName("create: startDate는 자정, endDate는 입력 LocalDate 그대로(getter 기준) 보존된다")
	void create_setsStartAtStartOfDay_andEndAtEndOfDay_observable() {
		// given
		LocalDate start = LocalDate.of(2025, 8, 20);
		LocalDate end   = LocalDate.of(2025, 8, 22);

		// when
		Medication m = Medication.create(
			"타이레놀",
			start,
			end,
			SoundType.CALL,
			MedicationType.BEAUTY,
			10L,
			2
		);

		// then (공개 API로 관찰 가능한 수준만 검증)
		assertThat(m.getStartDate()).isEqualTo(start);
		assertThat(m.getEndDate()).isEqualTo(end);
		assertThat(m.getMedicineName()).isEqualTo("타이레놀");
		assertThat(m.getUserId()).isEqualTo(10L);
		assertThat(m.getIntakeCount()).isEqualTo(2);
		assertThat(m.getMedicationType()).isEqualTo(MedicationType.BEAUTY);
		assertThat(m.getSoundType()).isEqualTo(SoundType.CALL);
	}

	@Test
	@DisplayName("create: endDate가 null이면 open-ended(무기한)으로 설정된다")
	void create_openEnded() {
		// given
		LocalDate start = LocalDate.of(2025, 8, 20);

		// when
		Medication m = Medication.create(
			"비타민C",
			start,
			null,
			SoundType.CALL,
			MedicationType.BEAUTY,
			1L,
			1
		);

		// then
		assertThat(m.getStartDate()).isEqualTo(start);
		assertThat(m.getEndDate()).isNull();
		// 상태는 now에 따라 달라지므로 다른 테스트에서 고정시간으로 검증
		assertThat(m.getMedicationStatus()).isIn(
			MedicationStatus.PLANNED,
			MedicationStatus.TAKING,
			MedicationStatus.ENDED
		);
	}

	@Test
	@DisplayName("getMedicationStatus: now가 시작 전 → PLANNED")
	void status_planned_beforeStart() {
		LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 19, 10, 0);
		LocalDate start = LocalDate.of(2025, 8, 20);
		LocalDate end   = LocalDate.of(2025, 8, 22);

		try (MockedStatic<LocalDateTime> mockedNow = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
			mockedNow.when(LocalDateTime::now).thenReturn(fixedNow);

			Medication m = Medication.create("A", start, end, SoundType.CALL, MedicationType.BEAUTY, 1L, 1);

			assertThat(m.getMedicationStatus()).isEqualTo(MedicationStatus.PLANNED);
		}
	}

	@Test
	@DisplayName("getMedicationStatus: now가 시작~종료(포함) 사이 → TAKING")
	void status_taking_betweenStartAndEndInclusive() {
		LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 21, 9, 0);
		LocalDate start = LocalDate.of(2025, 8, 20);
		LocalDate end   = LocalDate.of(2025, 8, 22);

		try (MockedStatic<LocalDateTime> mockedNow = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
			mockedNow.when(LocalDateTime::now).thenReturn(fixedNow);

			Medication m = Medication.create("A", start, end, SoundType.CALL, MedicationType.BEAUTY, 1L, 1);

			assertThat(m.getMedicationStatus()).isEqualTo(MedicationStatus.TAKING);
		}
	}

	@Test
	@DisplayName("getMedicationStatus: 종료일이 없으면 시작 이후 언제나 TAKING")
	void status_taking_openEnded() {
		LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 25, 12, 0);
		LocalDate start = LocalDate.of(2025, 8, 20);

		try (MockedStatic<LocalDateTime> mockedNow = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
			mockedNow.when(LocalDateTime::now).thenReturn(fixedNow);

			Medication m = Medication.create("A", start, null, SoundType.CALL, MedicationType.BEAUTY, 1L, 1);

			assertThat(m.getMedicationStatus()).isEqualTo(MedicationStatus.TAKING);
		}
	}

	@Test
	@DisplayName("getMedicationStatus: now가 종료 이후 → ENDED")
	void status_ended_afterEnd() {
		LocalDate start = LocalDate.of(2025, 8, 20);
		LocalDate end   = LocalDate.of(2025, 8, 22);
		LocalDateTime fixedNow = LocalDateTime.of(2025, 8, 23, 0, 0);

		try (MockedStatic<LocalDateTime> mockedNow = mockStatic(LocalDateTime.class, CALLS_REAL_METHODS)) {
			mockedNow.when(LocalDateTime::now).thenReturn(fixedNow);

			Medication m = Medication.create("A", start, end, SoundType.CALL, MedicationType.BEAUTY, 1L, 1);

			assertThat(m.getMedicationStatus()).isEqualTo(MedicationStatus.ENDED);
		}
	}

	@Test
	@DisplayName("end(): 종료일시 설정 후 getEndDate가 LocalDate를 반환한다")
	void end_setsEndDateTime() {
		LocalDate start = LocalDate.of(2025, 8, 20);
		Medication m = Medication.create("A", start, null, SoundType.CALL, MedicationType.BEAUTY, 1L, 1);

		// when
		m.end(LocalDateTime.of(2025, 8, 22, 10, 0));

		// then
		assertThat(m.getEndDate()).isEqualTo(LocalDate.of(2025, 8, 22));
	}
}
