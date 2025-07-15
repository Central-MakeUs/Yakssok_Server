package server.yakssok.domain.medication_schedule.domain.repository;

import java.time.LocalDate;
import java.util.List;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public interface MedicationScheduleQueryRepository {
	List<MedicationScheduleDto> findUserScheduleByDate(Long userId, LocalDate date);
	List<MedicationScheduleDto> findSchedulesInPastRange(Long userId, LocalDate startDate, LocalDate endDate);
}
