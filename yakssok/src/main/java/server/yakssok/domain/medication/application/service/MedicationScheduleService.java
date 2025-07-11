package server.yakssok.domain.medication.application.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.MedicationSchedule;
import server.yakssok.domain.medication.domain.repository.MedicationRepository;
import server.yakssok.domain.medication.domain.repository.MedicationScheduleJdbcRepository;
import server.yakssok.domain.medication.domain.repository.dto.MedicationScheduleDto;

@Service
@RequiredArgsConstructor
public class MedicationScheduleService {

	private final MedicationScheduleJdbcRepository medicationScheduleRepository;
	private final MedicationRepository medicationRepository;

	@Transactional
	public void generateTodaySchedules() {
		LocalDate today = LocalDate.now();
		DayOfWeek todayDayOfWeek = today.getDayOfWeek();

		List<MedicationScheduleDto> medicationScheduleDtos =
			medicationRepository.findMedicationsByDate(today, todayDayOfWeek);
		List<MedicationSchedule> schedules = medicationScheduleDtos.stream()
			.map(dto -> MedicationSchedule.create(
				dto.medicineName(),
				today,
				dto.intakeTime(),
				dto.medicationId()
			))
			.toList();

		medicationScheduleRepository.batchInsert(schedules); //TODO: 배치 인서트
	}
}
