package server.yakssok.domain.medication.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication.domain.entity.QMedicationIntakeDay.*;
import static server.yakssok.domain.medication.domain.entity.QMedicationIntakeTime.medicationIntakeTime;

import java.time.DayOfWeek;
import java.time.LocalDate;
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

	//TODO : 다 가져와서 가공하기
	@Override
	public List<Medication> findAllUserMedications(Long userId) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(medication.userId.eq(userId))
			.distinct()
			.orderBy(medication.id.desc())
			.fetch();
	}

	@Override
	public List<MedicationDto> findMedicationsByDate(LocalDateTime dateTime, DayOfWeek dayOfWeek) {
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
				isMedicationStarted(dateTime),
				isMedicationNotEnded(dateTime),
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
				isMedicationNotEnded(LocalDateTime.now())
			)
			.fetch();
	}

	private BooleanExpression isMedicationStarted(LocalDateTime dateTime) {
		return medication.startDateTime.loe(dateTime);
	}

	private BooleanExpression isMedicationNotEnded(LocalDateTime dateTime) {
		return medication.endDateTime.isNull()
			.or(medication.endDateTime.goe(dateTime));
	}

	private BooleanExpression isIntakeDayOfWeek(DayOfWeek targetDayOfWeek) {
		return medicationIntakeDay.dayOfWeek.eq(targetDayOfWeek);
	}
}
