package server.yakssok.domain.medication.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication.domain.entity.QMedicationIntakeDay.*;
import static server.yakssok.domain.medication.domain.entity.QMedicationIntakeTime.medicationIntakeTime;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.repository.dto.FutureMedicationSchedulesDto;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;

@RequiredArgsConstructor
public class MedicationQueryRepositoryImpl implements MedicationQueryRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Medication> findAllUserMedications(Long userId) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(isUser(userId))
			.distinct()
			.orderBy(medication.id.desc())
			.fetch();
	}

	@Override
	public List<Medication> findUserPlannedMedications(Long userId, LocalDateTime now) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(
				isUser(userId),
				isPlanned(now)
			)
			.distinct()
			.orderBy(medication.id.desc())
			.fetch();
	}

	@Override
	public List<Medication> findUserTakingMedications(Long userId, LocalDateTime now) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(
				isUser(userId),
				isTaking(now)
			)
			.distinct()
			.orderBy(medication.id.desc())
			.fetch();
	}

	@Override
	public List<Medication> findUserEndedMedications(Long userId, LocalDateTime now) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(
				isUser(userId),
				isEnded(now)
			)
			.distinct()
			.orderBy(medication.id.desc())
			.fetch();
	}


	@Override
	public List<MedicationDto> findMedicationsForScheduleGeneration(LocalDateTime dateTime, DayOfWeek dayOfWeek) {
		return queryFactory
			.select(Projections.constructor(
				MedicationDto.class,
				medication.id,
				medication.medicineName,
				medicationIntakeTime.time,
				medication.userId
			))
			.from(medication)
			.join(medication.intakeDays, medicationIntakeDay)
			.join(medication.intakeTimes, medicationIntakeTime)
			.where(
				isTaking(dateTime),
				isIntakeDayOfWeek(dayOfWeek)
			)
			.fetch();
	}

	@Override
	public List<FutureMedicationSchedulesDto> findFutureMedicationSchedules(Long userId) {
		return queryFactory
			.select(Projections.constructor(
				FutureMedicationSchedulesDto.class,
				medication,
				medicationIntakeDay,
				medicationIntakeTime
			))
			.from(medication)
			.leftJoin(medicationIntakeTime).on(medicationIntakeTime.medication.id.eq(medication.id))
			.leftJoin(medicationIntakeDay).on(medicationIntakeDay.medication.id.eq(medication.id))
			.where(
				isUser(userId),
				isEnded(LocalDateTime.now()).not()
			)
			.fetch();
	}

	private BooleanExpression isIntakeDayOfWeek(DayOfWeek targetDayOfWeek) {
		return medicationIntakeDay.dayOfWeek.eq(targetDayOfWeek);
	}

	private BooleanExpression isUser(Long userId) {
		return medication.userId.eq(userId);
	}

	private BooleanExpression isPlanned(LocalDateTime now) {
		return medication.startDateTime.gt(now);
	}

	private BooleanExpression isTaking(LocalDateTime now) {
		return medication.startDateTime.loe(now)
			.and(
				medication.endDateTime.isNull()
					.or(medication.endDateTime.goe(now))
			);
	}

	private BooleanExpression isEnded(LocalDateTime now) {
		return medication.endDateTime.isNotNull()
			.and(medication.endDateTime.lt(now));
	}
}
