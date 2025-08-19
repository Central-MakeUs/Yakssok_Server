package server.yakssok.domain.medication.application.service;


import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.hibernate.validator.internal.util.Contracts.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.InstanceOfAssertFactories.type;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import server.yakssok.domain.medication.application.exception.MedicationException;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.entity.MedicationStatus;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeDayRepository;
import server.yakssok.domain.medication.domain.repository.MedicationIntakeTimeRepository;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.MedicationGroupedResponse;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.global.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {
	@Mock private MedicationRepository medicationRepository;
	@Mock private MedicationIntakeDayRepository medicationIntakeDayRepository;
	@Mock private MedicationIntakeTimeRepository medicationIntakeTimeRepository;
	@Mock private MedicationScheduleService medicationScheduleService;
	@InjectMocks private MedicationService medicationService;
	private static final Long USER_ID = 69L;

	@Nested
	@DisplayName("findMedications")
	class FindMedications {

		@Test
		@DisplayName("status가 null/공백이면 findAllUserMedications 호출")
		void find_all_when_status_absent() {
			when(medicationRepository.findAllUserMedications(USER_ID)).thenReturn(List.of());

			MedicationGroupedResponse res1 = medicationService.findMedications(USER_ID, null);
			MedicationGroupedResponse res2 = medicationService.findMedications(USER_ID, "  ");

			assertNotNull(res1);
			assertNotNull(res2);
			verify(medicationRepository, times(2)).findAllUserMedications(USER_ID);
			verifyNoMoreInteractions(medicationRepository);
		}

		@Test
		@DisplayName("status=PLANNED이면 findUserPlannedMedications 호출")
		void find_planned() {
			when(medicationRepository.findUserPlannedMedications(eq(USER_ID), any(LocalDateTime.class)))
				.thenReturn(List.of());

			MedicationGroupedResponse res = medicationService.findMedications(USER_ID, MedicationStatus.PLANNED.name());

			assertNotNull(res);
			verify(medicationRepository).findUserPlannedMedications(eq(USER_ID), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("status=TAKING이면 findUserTakingMedications 호출")
		void find_taking() {
			when(medicationRepository.findUserTakingMedications(eq(USER_ID), any(LocalDateTime.class)))
				.thenReturn(List.of());

			MedicationGroupedResponse res = medicationService.findMedications(USER_ID, MedicationStatus.TAKING.name());

			assertNotNull(res);
			verify(medicationRepository).findUserTakingMedications(eq(USER_ID), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("status=ENDED이면 findUserEndedMedications 호출")
		void find_ended() {
			when(medicationRepository.findUserEndedMedications(eq(USER_ID), any(LocalDateTime.class)))
				.thenReturn(List.of());

			MedicationGroupedResponse res = medicationService.findMedications(USER_ID, MedicationStatus.ENDED.name());

			assertNotNull(res);
			verify(medicationRepository).findUserEndedMedications(eq(USER_ID), any(LocalDateTime.class));
		}
	}

	@Nested
	@DisplayName("createMedication")
	class CreateMedication {

		@Test
		@DisplayName("오늘 시작이고 오늘 요일이 섭취일이면 TodaySchedules 생성")
		void create_triggers_today_schedules_when_today_start_and_match_day() {
			// given
			CreateMedicationRequest req = mock(CreateMedicationRequest.class);
			Medication med = mock(Medication.class);

			LocalDate today = LocalDate.now();
			when(med.getStartDate()).thenReturn(today);

			when(req.toMedication(USER_ID)).thenReturn(med);

			List<MedicationIntakeTime> times = List.of(mock(MedicationIntakeTime.class));
			List<MedicationIntakeDay> days = List.of(mock(MedicationIntakeDay.class));
			when(req.toMedicationsTimes(med)).thenReturn(times);
			when(req.toIntakeDays(med)).thenReturn(days);

			when(req.intakeDays()).thenReturn(List.of(today.getDayOfWeek()));
			List<LocalTime> intakeTimes = List.of(LocalTime.of(9, 0));
			when(req.intakeTimes()).thenReturn(intakeTimes);

			// when
			medicationService.createMedication(USER_ID, req);

			// then
			verify(medicationRepository).save(med);
			verify(medicationIntakeTimeRepository).saveAll(times);
			verify(medicationIntakeDayRepository).saveAll(days);
			verify(medicationScheduleService).createTodaySchedules(med, intakeTimes);
			verifyNoMoreInteractions(medicationScheduleService);
		}

		@Test
		@DisplayName("오늘 시작이 아니거나 요일이 불일치면 TodaySchedules 생성하지 않음")
		void create_does_not_trigger_when_not_today_or_not_matching_day() {
			CreateMedicationRequest req = mock(CreateMedicationRequest.class);
			Medication med = mock(Medication.class);

			when(med.getStartDate()).thenReturn(LocalDate.now().minusDays(1));
			when(req.toMedication(USER_ID)).thenReturn(med);
			when(req.toMedicationsTimes(med)).thenReturn(List.of());
			when(req.toIntakeDays(med)).thenReturn(List.of());

			// 항상 오늘이 아닌 요일로 설정
			DayOfWeek today = LocalDate.now().getDayOfWeek();
			DayOfWeek notToday = DayOfWeek.of(today.getValue() % 7 + 1);
			when(req.intakeDays()).thenReturn(List.of(notToday));

			// when
			medicationService.createMedication(USER_ID, req);

			// then
			verify(medicationRepository).save(med);
			verify(medicationIntakeTimeRepository).saveAll(anyList());
			verify(medicationIntakeDayRepository).saveAll(anyList());
			verify(medicationScheduleService, never()).createTodaySchedules(any(), any());

			verify(req, never()).intakeTimes();
		}
	}

	@Nested
	@DisplayName("endMedication")
	class EndMedication {

		@Test
		@DisplayName("정상 종료 시 medication.end 호출 및 오늘 이후 스케줄 삭제")
		void end_ok() {
			Long medId = 101L;
			Medication med = mock(Medication.class);
			when(medicationRepository.findById(medId)).thenReturn(Optional.of(med));

			medicationService.endMedication(medId);

			verify(med).end(any(LocalDateTime.class));
			verify(medicationScheduleService).deleteTodayUpcomingSchedules(eq(medId), any(LocalDateTime.class));
		}

		@Test
		@DisplayName("존재하지 않으면 MedicationException(존재하지 않는 복약 스케줄입니다.)")
		void end_not_found() {
			Long medId = 404L;
			when(medicationRepository.findById(medId)).thenReturn(Optional.empty());

			assertThatThrownBy(() -> medicationService.endMedication(medId))
				.asInstanceOf(type(MedicationException.class))
				.extracting(MedicationException::getResponseCode)
				.isEqualTo(ErrorCode.NOT_FOUND_MEDICATION);
		}
	}

	@Nested
	@DisplayName("deleteAllByUserId")
	class DeleteAll {

		@Test
		@DisplayName("유저의 약 목록/연관 요일·시간·스케줄 일괄 삭제")
		void delete_all_cascade() {
			Medication m1 = mock(Medication.class);
			Medication m2 = mock(Medication.class);
			when(m1.getId()).thenReturn(1L);
			when(m2.getId()).thenReturn(2L);
			List<Medication> meds = List.of(m1, m2);

			when(medicationRepository.findAllUserMedications(USER_ID)).thenReturn(meds);

			medicationService.deleteAllByUserId(USER_ID);

			verify(medicationIntakeDayRepository).deleteAllByMedicationIds(List.of(1L, 2L));
			verify(medicationIntakeTimeRepository).deleteAllByMedicationIds(List.of(1L, 2L));
			verify(medicationRepository).deleteAll(meds);
			verify(medicationScheduleService).deleteAllByMedicationIds(List.of(1L, 2L));
		}
	}
}