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
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;
import server.yakssok.domain.medication_schedule.domain.repository.dto.RemainingMedicationDto;

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
	public List<MedicationScheduleDto> findRemainingMedicationDetail(
		Long userId,
		LocalDateTime now
	) {
		return jpaQueryFactory
			.select(SCHEDULE_DTO_PROJECTION)
			.from(medicationSchedule)
			.innerJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.eq(now.toLocalDate()),
				medicationSchedule.scheduledTime.loe(now.toLocalTime()),
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
	public List<RemainingMedicationDto> findTodayRemainingMedications(
		List<Long> followingIds,
		LocalDateTime delayBoundaryTime
	) {
		LocalTime localTime = delayBoundaryTime.toLocalTime();
		LocalDate localDate = delayBoundaryTime.toLocalDate();
		return jpaQueryFactory
			.select(Projections.constructor(
				RemainingMedicationDto.class,
				medicationSchedule.userId,
				medicationSchedule.scheduledDate,
				medicationSchedule.scheduledTime
			))
			.from(medicationSchedule)
			.where(
				medicationSchedule.userId.in(followingIds),
				medicationSchedule.scheduledDate.eq(localDate),
				medicationSchedule.scheduledTime.loe(localTime),
				medicationSchedule.isTaken.isFalse()
			)
			.orderBy(medicationSchedule.userId.asc(), medicationSchedule.scheduledTime.asc())
			.fetch();
	}
}
