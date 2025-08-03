package server.yakssok.domain.medication_schedule.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule.*;
import static server.yakssok.domain.user.domain.entity.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
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
	public Map<Long, Integer> countTodayRemainingMedications(List<Long> followingIdsWithTodaySchedule, LocalDateTime now) {
		return jpaQueryFactory
			.select(medication.userId, medicationSchedule.count())
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.in(followingIdsWithTodaySchedule),
				medicationSchedule.scheduledDate.eq(now.toLocalDate()),
				medicationSchedule.scheduledTime.lt(now.toLocalTime()),
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
	public List<MedicationScheduleDto> findRemainingMedicationDetail(Long userId, LocalDateTime now) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.eq(now.toLocalDate()),
				medicationSchedule.scheduledTime.lt(now.toLocalTime()),
				medicationSchedule.isTaken.isFalse()
			)
			.orderBy(
				medicationSchedule.scheduledTime.asc()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleAlarmDto> findNotTakenSchedules(LocalDateTime notTakenLimitTime) {
		return jpaQueryFactory
			.select(Projections.constructor(
				MedicationScheduleAlarmDto.class,
				medicationSchedule.id,
				medication.medicineName,
				user.id,
				user.nickName,
				medication.soundType
			))
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.innerJoin(user).on(user.id.eq(medication.userId))
			.where(
				medicationSchedule.scheduledDate.eq(notTakenLimitTime.toLocalDate()),
				medicationSchedule.scheduledTime.hour().eq(notTakenLimitTime.getHour()),
				medicationSchedule.scheduledTime.minute().eq(notTakenLimitTime.getMinute()),
				medicationSchedule.isTaken.isFalse()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleAlarmDto> findSchedules(LocalDateTime intakeTime) {
		return jpaQueryFactory
			.select(Projections.constructor(
				MedicationScheduleAlarmDto.class,
				medicationSchedule.id,
				medication.medicineName,
				user.id,
				user.nickName,
				medication.soundType
			))
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.innerJoin(user).on(user.id.eq(medication.userId))
			.where(
				medicationSchedule.scheduledDate.eq(intakeTime.toLocalDate()),
				medicationSchedule.scheduledTime.hour().eq(intakeTime.getHour()),
				medicationSchedule.scheduledTime.minute().eq(intakeTime.getMinute())
			)
			.fetch();
	}

	@Override
	public List<Long> findUserIdsWithAllTakenToday(List<Long> followingIds, LocalDate now) {
		return jpaQueryFactory
				.select(medication.userId)
				.from(medicationSchedule)
				.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
				.where(
					medication.userId.in(followingIds),
					medicationSchedule.scheduledDate.eq(now)
				)
				.groupBy(medication.userId)
				.having(
					medicationSchedule.count().eq(
						new CaseBuilder()
							.when(medicationSchedule.isTaken.isTrue()).then(1L)
							.otherwise(0L)
							.sum()
					)
				)
				.fetch();
	}
}
