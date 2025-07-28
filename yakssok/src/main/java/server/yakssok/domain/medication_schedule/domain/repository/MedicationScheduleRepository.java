package server.yakssok.domain.medication_schedule.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long>, MedicationScheduleQueryRepository {
	@Modifying
	@Query("DELETE FROM MedicationSchedule ms WHERE ms.medicationId IN :medicationIds")
	void deleteAllByMedicationIds(List<Long> medicationIds);
}
