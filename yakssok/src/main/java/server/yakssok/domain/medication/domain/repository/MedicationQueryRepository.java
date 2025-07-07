package server.yakssok.domain.medication.domain.repository;

import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;

public interface MedicationQueryRepository {
	List<Medication> findAllUserMedications(Long userId);
}
