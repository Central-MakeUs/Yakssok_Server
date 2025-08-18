package server.yakssok.domain.medication.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;

@Repository
public interface MedicationIntakeTimeRepository extends JpaRepository<MedicationIntakeTime, Long> {
	@Modifying
	@Query("DELETE FROM MedicationIntakeTime t WHERE t.medication.id IN :medicationIds")
	void deleteAllByMedicationIds(List<Long> medicationIds);
}
