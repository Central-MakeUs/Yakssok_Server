package server.yakssok.domain.medication_schedule.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication_schedule.domain.entity.QMedicationSchedule.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.domain.repository.dto.MedicationScheduleDto;

@RequiredArgsConstructor
public class MedicationScheduleQueryRepositoryImpl implements MedicationScheduleQueryRepository{
	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<MedicationScheduleDto> findUserScheduleByDate(
		Long userId, LocalDate date
	) {
		return jpaQueryFactory
			.select(Projections.constructor(
				MedicationScheduleDto.class,
				medicationSchedule.scheduledDate,
				medicationSchedule.id,
				medication.medicationType,
				medication.medicineName,
				medicationSchedule.scheduledTime,
				medicationSchedule.isTaken
			))
			.from(medicationSchedule)
			.leftJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.eq(date)
			)
			.orderBy(
				medicationSchedule.isTaken.asc(),
				medicationSchedule.scheduledTime.asc())
			.fetch();
	}

	@Override
	public List<MedicationScheduleDto> findSchedulesInPastRange(
		Long userId, LocalDate startDate,
		LocalDate endDate
	) {
		return jpaQueryFactory
			.select(Projections.constructor(
				MedicationScheduleDto.class,
				medicationSchedule.scheduledDate,
				medicationSchedule.id,
				medication.medicationType,
				medication.medicineName,
				medicationSchedule.scheduledTime,
				medicationSchedule.isTaken
			))
			.from(medicationSchedule)
			.leftJoin(medication).on(medication.id.eq(medicationSchedule.medicationId))
			.where(
				medication.userId.eq(userId),
				medicationSchedule.scheduledDate.goe(startDate),
				medicationSchedule.scheduledDate.loe(endDate)
			)
			.fetch();
	}

	@Override
	public void deleteTodayUpcomingSchedules(Long medicationId, LocalDate todayDate, LocalTime currentTime) {
		jpaQueryFactory
			.delete(medicationSchedule)
			.where(
				medicationSchedule.medicationId.eq(medicationId),
				medicationSchedule.scheduledDate.eq(todayDate),
				medicationSchedule.scheduledTime.after(currentTime)
			)
			.execute(); // 삭제된 row 개수 리턴
	}
}
