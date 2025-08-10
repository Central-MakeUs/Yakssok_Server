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
	List<Long> findUserIdsWithAllTakenToday(List<Long> followingIds, LocalDate now);
	List<MedicationScheduleDto> findRemainingMedicationDetail(Long friendId, LocalDateTime now);
	List<MedicationScheduleAlarmDto> findNotTakenSchedules(LocalDateTime notTakenLimitTime);
	List<MedicationScheduleAlarmDto> findSchedules(LocalDateTime now);
	List<RemainingMedicationDto> findTodayRemainingMedications(List<Long> followingIds, LocalDateTime now);
}
