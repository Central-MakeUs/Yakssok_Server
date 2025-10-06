package server.yakssok.domain.medication_schedule.domain.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public interface MedicationScheduleQueryRepository {
	List<MedicationScheduleDto> findUserSchedulesByDate(Long userId, LocalDate date);
	List<MedicationScheduleDto> findUserSchedulesInPastRange(Long userId, LocalDate startDate, LocalDate endDate);
	void deleteTodayUpcomingSchedules(Long medicationId, LocalDate currentDate, LocalTime currentTime);
	List<MedicationScheduleAlarmDto> findNotTakenSchedules(LocalDateTime notTakenLimitTime);
	List<MedicationScheduleAlarmDto> findTodayNotTakenSchedules(LocalDateTime notTakenLimitTime);
	List<MedicationScheduleAlarmDto> findSchedules(LocalDateTime now);
	List<MedicationScheduleDto> findTodayRemainingMedications(List<Long> followingIds, LocalDateTime now);
	List<MedicationScheduleDto> findTodayAllTakenSchedules(List<Long> followingIds, LocalDate today);
}
