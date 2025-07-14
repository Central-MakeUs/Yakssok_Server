package server.yakssok.domain.medication_schedule.domain.repository;

import java.time.LocalDate;
import java.util.List;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public interface MedicationScheduleQueryRepository {
	List<MedicationScheduleDto> findUserSchedule(Long userId, LocalDate date);
}
