package server.yakssok.domain.medication_schedule.domain.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

public interface MedicationScheduleQueryRepository {
	List<MedicationScheduleDto> findUserSchedulesByDate(Long userId, LocalDate date);
	List<MedicationScheduleDto> findUserSchedulesInPastRange(Long userId, LocalDate startDate, LocalDate endDate);
	void deleteTodayUpcomingSchedules(Long medicationId, LocalDate currentDate, LocalTime currentTime);
	List<Long> findFollowingIdsWithTodaySchedule(List<Long> followingIds, LocalDate now);
	Map<Long, Integer> countTodayRemainingMedications(List<Long> followingIdsWithTodaySchedule, LocalDate now);
	List<MedicationScheduleDto> findRemainingMedicationDetail(Long friendId, LocalDate now);
}
