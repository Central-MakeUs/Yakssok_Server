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
		@DisplayName("복약 생성 시 약/요일/시간/스케줄 저장")
		void create_success() {
			// given
			CreateMedicationRequest request = mock(CreateMedicationRequest.class);
			Medication medication = mock(Medication.class);

			List<MedicationIntakeTime> times = List.of(mock(MedicationIntakeTime.class));
			List<MedicationIntakeDay> days = List.of(mock(MedicationIntakeDay.class));

			when(request.toMedication(USER_ID)).thenReturn(medication);
			when(medicationRepository.save(medication)).thenReturn(medication);
			when(request.toMedicationsTimes(medication)).thenReturn(times);
			when(request.toIntakeDays(medication)).thenReturn(days);
			when(request.intakeTimes()).thenReturn(List.of(LocalTime.of(9, 0)));

			// when
			medicationService.createMedication(USER_ID, request);

			// then
			verify(medicationRepository).save(medication);
			verify(medicationIntakeTimeRepository).saveAll(times);
			verify(medicationIntakeDayRepository).saveAll(days);
			verify(medicationScheduleService)
				.createAllSchedules(eq(medication), anyList());
		}
	}


	@Nested
	@DisplayName("endMedication")
	class EndMedication {

		@Test
		@DisplayName("복약 종료 시 end 호출 및 이후 스케줄 삭제")
		void end_success() {
			// given
			Long medId = 1L;
			Medication medication = mock(Medication.class);

			when(medicationRepository.findById(medId))
				.thenReturn(Optional.of(medication));

			// when
			medicationService.endMedication(medId);

			// then
			verify(medication).end(any(LocalDateTime.class));
			verify(medicationScheduleService)
				.deleteAllUpcomingSchedules(eq(medId), any(LocalDateTime.class));
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