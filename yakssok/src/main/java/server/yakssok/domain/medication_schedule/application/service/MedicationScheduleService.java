package server.yakssok.domain.medication_schedule.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;
import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication_schedule.domain.repository.MedicationScheduleRepository;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleGroupResponse;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleResponse;

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
		List<MedicationScheduleDto> userSchedule = medicationScheduleRepository.findUserSchedule(userId, today);
		List<TodayMedicationScheduleResponse> todayMedicationScheduleResponses =
			userSchedule.stream()
				.map(
					medicationScheduleDto -> {
						String date = String.valueOf(medicationScheduleDto.date());
						Long scheduleId = medicationScheduleDto.scheduleId();
						String medicationType = medicationScheduleDto.medicationType().name();
						String medicationName = medicationScheduleDto.medicationName();
						String localTime = String.valueOf(medicationScheduleDto.intakeTime());
						boolean taken = medicationScheduleDto.isTaken();
						return TodayMedicationScheduleResponse.from(
							date,
							scheduleId,
							medicationType,
							medicationName,
							localTime,
							taken
						);
					}
				).toList();
		return TodayMedicationScheduleGroupResponse.of(todayMedicationScheduleResponses);
	}
}
