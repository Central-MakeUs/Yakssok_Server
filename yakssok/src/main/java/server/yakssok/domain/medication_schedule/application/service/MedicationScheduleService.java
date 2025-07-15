package server.yakssok.domain.medication_schedule.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;
import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.domain.repository.dto.FutureMedicationSchedulesDto;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleResponse;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleJdbcRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationRepository medicationRepository;

	@Transactional
	public void generateTodaySchedules() {
		LocalDate today = LocalDate.now();
		DayOfWeek todayDayOfWeek = today.getDayOfWeek();

		List<MedicationDto> medicationScheduleDtos =
			medicationRepository.findMedicationsByDate(today, todayDayOfWeek);
		List<MedicationSchedule> schedules = medicationScheduleDtos.stream()
			.map(dto -> MedicationSchedule.create(
				today,
				dto.intakeTime(),
				dto.medicationId()
			))
			.toList();
		medicationScheduleJdbcRepository.batchInsert(schedules);
	}

	@Transactional(readOnly = true)
	public TodayMedicationScheduleGroupResponse findTodayMedicationSchedule(Long userId) {
		LocalDate today = LocalDate.now();
		List<MedicationScheduleDto> userSchedule = medicationScheduleRepository.findUserMedicationSchedule(userId, today);
		List<TodayMedicationScheduleResponse> medicationScheduleResponses = convertToResponse(userSchedule);
		return TodayMedicationScheduleGroupResponse.of(medicationScheduleResponses);
	}

	private static List<TodayMedicationScheduleResponse> convertToResponse(List<MedicationScheduleDto> userSchedule) {
		List<TodayMedicationScheduleResponse> todayMedicationScheduleResponses =
			userSchedule.stream()
				.map(schedule -> TodayMedicationScheduleResponse.from(schedule))
				.toList();
		return todayMedicationScheduleResponses;
	}

	@Transactional
	public void takeMedication(Long userId, Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new MedicationScheduleException(ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE));
		schedule.take();
	}

	@Transactional(readOnly = true)
	public TodayMedicationScheduleGroupResponse findRangeMedicationSchedule(Long userId, String startDate, String endDate) {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		LocalDate today = LocalDate.now();
		List<MedicationScheduleDto> pastAndTodaySchedules = new ArrayList<>();
		List<MedicationScheduleDto> futureSchedules = new ArrayList<>();

		// Case 1: 시작~종료 둘 다 과거/오늘(오늘 이하)
		if (!end.isAfter(today)) {
			pastAndTodaySchedules = medicationScheduleRepository.findRangeMedicationSchedule(userId, start, end);
		}
		// Case 2: 시작이 과거/오늘, 종료가 미래(오늘 미만, 이상 섞여 있음)
		else if (!start.isAfter(today) && end.isAfter(today)) {
			pastAndTodaySchedules = medicationScheduleRepository.findRangeMedicationSchedule(userId, start, today);
			futureSchedules = createFutureScheduleDtos(userId, today.plusDays(1), end);
		}
		// Case 3: 시작~종료 모두 미래(today 미만)
		else if (start.isAfter(today)) {
			futureSchedules = createFutureScheduleDtos(userId, start, end);
		}

		// 과거 + 미래 DTO 합치기 (TODO : 날짜, 시간으로 정렬 -> map으로 변환(dto로 밀어넣기))
		List<TodayMedicationScheduleResponse> todayMedicationScheduleResponses =
			Stream.concat(
					pastAndTodaySchedules.stream().map(TodayMedicationScheduleResponse::from),
					futureSchedules.stream().map(TodayMedicationScheduleResponse::from)
				)
				.sorted(
					Comparator.comparing(r -> LocalTime.parse(r.intakeTime()))
				)
				.toList();
		return TodayMedicationScheduleGroupResponse.of(todayMedicationScheduleResponses);
	}

	private List<MedicationScheduleDto> createFutureScheduleDtos(Long userId, LocalDate start, LocalDate end) {
		//db에서 복약 정보를 가져옴(userId) 확인 - 완료 상태인 Medication은 제외, 계획이거나 복용 중인 Medication만 가져오기
		// MedicationIntakeDay, MedicationIntakeTime와 조인해서 dto로 가져오기
		List<FutureMedicationSchedulesDto> schedules = medicationRepository.findFutureMedicationSchedules(userId);
		List<MedicationScheduleDto> result = new ArrayList<>();

		// start부터 end까지의 날짜에 대해 복약 스케줄을 생성  (이때 종료일을 확인하기! 종료일까지만 만들기)
		for (FutureMedicationSchedulesDto schedule : schedules) {
			Medication medication = schedule.medication();
			MedicationIntakeDay intakeDay = schedule.medicationIntakeDay();
			MedicationIntakeTime intakeTime = schedule.medicationIntakeTime();

			LocalDate routineStart = medication.getStartDate();
			LocalDate routineEnd = medication.getEndDate();
			LocalDate actualStart = start.isAfter(routineStart) ? start : routineStart;
			LocalDate actualEnd = (routineEnd == null) ? end : (end.isBefore(routineEnd) ? end : routineEnd);

			if (actualEnd.isBefore(actualStart)) continue;

			for (LocalDate date = actualStart; !date.isAfter(actualEnd); date = date.plusDays(1)) {
				if (intakeDay.getDayOfWeek() == date.getDayOfWeek()) {
					result.add(new MedicationScheduleDto(
						date,
						null,
						medication.getMedicationType(),
						medication.getMedicineName(),
						intakeTime.getTime(),
						false
					));
				}
			}
		}
		return result;
	}
}
