package server.yakssok.domain.medication.domain.repository;

import static server.yakssok.domain.medication.domain.entity.QMedication.*;
import static server.yakssok.domain.medication.domain.entity.QMedicationIntakeDay.*;

import java.util.List;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.domain.entity.Medication;

@RequiredArgsConstructor
public class MedicationQueryRepositoryImpl implements MedicationQueryRepository{
	private final JPAQueryFactory queryFactory;

	@Override
	public List<Medication> findAllUserMedications(Long userId) {
		return queryFactory
			.selectFrom(medication)
			.leftJoin(medication.intakeDays, medicationIntakeDay).fetchJoin()
			.where(medication.user.id.eq(userId))
			.distinct()
			.fetch();
	}
}
