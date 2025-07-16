package server.yakssok.domain.medication_schedule.application.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationScheduleGenerator;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleResponse;
import server.yakssok.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleJdbcRepository;
	private final MedicationScheduleRepository medicationScheduleRepository;
	private final MedicationScheduleFinder medicationScheduleFinder;
	private final MedicationScheduleGenerator medicationScheduleGenerator;

	@Transactional
	public void generateTodaySchedules() {
		LocalDate today = LocalDate.now();
		List<MedicationSchedule> schedules = medicationScheduleGenerator.generateTodaySchedules(today);
		medicationScheduleJdbcRepository.batchInsert(schedules);
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findTodayMedicationSchedule(Long userId) {
		List<MedicationScheduleDto> schedules = medicationScheduleFinder.findUserScheduleByDate(userId, LocalDate.now());
		return MedicationScheduleGroupResponse.fromList(convertToResponses(schedules));
	}

	private List<MedicationScheduleResponse> convertToResponses(List<MedicationScheduleDto> schedules) {
		return schedules.stream()
			.map(MedicationScheduleResponse::from)
			.toList();
	}

	@Transactional
	public void takeMedication(Long userId, Long scheduleId) {
		MedicationSchedule schedule = medicationScheduleRepository.findById(scheduleId)
			.orElseThrow(() -> new MedicationScheduleException(ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE));
		schedule.take();
	}

	@Transactional(readOnly = true)
	public MedicationScheduleGroupResponse findRangeMedicationSchedule(Long userId, String startDate, String endDate) {
		LocalDate start = LocalDate.parse(startDate);
		LocalDate end = LocalDate.parse(endDate);
		LocalDate today = LocalDate.now();
		if (start.isAfter(end)) {
			throw new MedicationScheduleException(ErrorCode.INVALID_INPUT_VALUE);
		}

		List<MedicationScheduleDto> schedules =
			medicationScheduleFinder.findSchedulesInPeriod(userId, start, end, today);
		return MedicationScheduleGroupResponse.fromList(sortedResponses(schedules));
	}

	private List<MedicationScheduleResponse> sortedResponses(List<MedicationScheduleDto> schedules) {
		return schedules.stream()
			.map(MedicationScheduleResponse::from)
			.sorted(
				Comparator.comparing(MedicationScheduleResponse::isTaken)
					.thenComparing(MedicationScheduleResponse::intakeTime)
			)
			.toList();
	}
}
