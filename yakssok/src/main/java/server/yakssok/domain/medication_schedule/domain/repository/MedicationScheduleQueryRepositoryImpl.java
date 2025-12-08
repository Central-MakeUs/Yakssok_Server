package server.yakssok.domain.medication_schedule.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule.*;
import static server.yakssok.domain.user.domain.entity.QUser.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule;
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
			medicationSchedule.isTaken,
			medication.userId
		);

	@Override
	public List<MedicationScheduleDto> findUserSchedulesByDate(Long userId, LocalDate date) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medicationSchedule.userId.eq(userId),
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
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medicationSchedule.userId.eq(userId),
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
				medicationSchedule.scheduledTime.before(notTakenLimitTime.toLocalTime()),
				medicationSchedule.isTaken.isFalse()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleAlarmDto> findTodayNotTakenSchedules(LocalDateTime notTakenLimitTime) {
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
				medicationSchedule.scheduledTime.loe(notTakenLimitTime.toLocalTime()),
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
				medicationSchedule.scheduledTime.minute().eq(intakeTime.getMinute()),
				medicationSchedule.isTaken.isFalse()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleDto> findTodayRemainingMedications(
		List<Long> followingIds,
		LocalDateTime delayBoundaryTime
	) {
		LocalTime localTime = delayBoundaryTime.toLocalTime();
		LocalDate localDate = delayBoundaryTime.toLocalDate();
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.from(medicationSchedule)
			.where(
				medication.userId.in(followingIds),
				medicationSchedule.scheduledDate.eq(localDate),
				medicationSchedule.scheduledTime.loe(localTime),
				medicationSchedule.isTaken.isFalse()
			)
			.fetch();
	}

	@Override
	public List<MedicationScheduleDto> findTodayAllTakenSchedules(List<Long> followingIds, LocalDate today) {
		QMedicationSchedule msNotTaken = new QMedicationSchedule("ms_not_taken");
		QMedicationSchedule msAnyToday = new QMedicationSchedule("ms_any_today");

		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medicationSchedule.userId.in(followingIds),
				medicationSchedule.scheduledDate.eq(today),

				// 오늘 미복용 스케줄이 하나도 없어야 함
				JPAExpressions.selectOne()
					.from(msNotTaken)
					.where(
						msNotTaken.userId.eq(medicationSchedule.userId),
						msNotTaken.scheduledDate.eq(today),
						msNotTaken.isTaken.isFalse()
					)
					.notExists(),

				// 오늘 스케줄이 최소 1개는 있어야 함 (빈 유저 제외)
				JPAExpressions.selectOne()
					.from(msAnyToday)
					.where(
						msAnyToday.userId.eq(medicationSchedule.userId),
						msAnyToday.scheduledDate.eq(today)
					)
					.exists()
			)
			.fetch();
	}
}
