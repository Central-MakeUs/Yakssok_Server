package server.yakssok.domain.medication_schedule.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@RequiredArgsConstructor
public class MedicationScheduleQueryRepositoryImpl implements MedicationScheduleQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	private static final ConstructorExpression<MedicationScheduleDto> SCHEDULE_DTO_PROJECTION =
		Projections.constructor(
			MedicationScheduleDto.class,
			medicationSchedule.scheduledDate,
			medicationSchedule.id,
			medication.medicationType,
			medication.medicineName,
			medicationSchedule.scheduledTime,
			medicationSchedule.isTaken
		);

	@Override
	public List<MedicationScheduleDto> findUserSchedulesByDate(Long userId, LocalDate date) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.eq(date)
			)
			.orderBy(
				medicationSchedule.isTaken.asc(),
				medicationSchedule.scheduledTime.asc()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleDto> findUserSchedulesInPastRange(Long userId, LocalDate startDate, LocalDate endDate) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.leftJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.between(startDate, endDate)
			)
			.orderBy(
				medicationSchedule.scheduledDate.asc(),
				medicationSchedule.scheduledTime.asc()
			)
			.fetch();
	}

	@Override
	public void deleteTodayUpcomingSchedules(Long medicationId, LocalDate currentDate, LocalTime currentTime) {
		jpaQueryFactory
			.delete(medicationSchedule)
			.where(
				medicationSchedule.medicationId.eq(medicationId),
				medicationSchedule.scheduledDate.eq(currentDate),
				medicationSchedule.scheduledTime.after(currentTime)
			)
			.execute();
	}

	@Override
	public boolean existsTodayScheduleByUserId(Long id) {
		return jpaQueryFactory
			.selectOne()
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(id),
				medicationSchedule.scheduledDate.eq(LocalDate.now())
			)
			.fetchFirst() != null;
	}

	@Override
	public List<Long> findFollowingIdsWithTodaySchedule(List<Long> followingIds, LocalDate now) {
		return jpaQueryFactory
			.select(medication.userId)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.in(followingIds),
				medicationSchedule.scheduledDate.eq(now)
			)
			.fetch();
	}

	@Override
	public Map<Long, Integer> countTodayRemainingMedications(List<Long> followingIdsWithTodaySchedule, LocalDate now) {
		return jpaQueryFactory
			.select(medication.userId, medicationSchedule.count())
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.in(followingIdsWithTodaySchedule),
				medicationSchedule.scheduledDate.eq(now),
				medicationSchedule.isTaken.isFalse()
			)
			.groupBy(medication.userId)
			.fetch()
			.stream()
			.collect(Collectors.toMap(
				tuple -> tuple.get(0, Long.class),
				tuple -> tuple.get(1, Long.class).intValue()
			));
	}

	@Override
	public List<MedicationScheduleDto> findRemainingMedicationDetail(Long userId, LocalDate now) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.eq(now),
				medicationSchedule.isTaken.isFalse()
			)
			.orderBy(
				medicationSchedule.scheduledTime.asc()
			)
			.fetch();
	}

}
