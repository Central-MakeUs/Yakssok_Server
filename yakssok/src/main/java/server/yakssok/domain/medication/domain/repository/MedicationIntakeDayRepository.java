package server.yakssok.domain.medication.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;

@Repository
public interface MedicationIntakeDayRepository extends JpaRepository<MedicationIntakeDay, Long> {
	@Modifying
	@Query("DELETE FROM MedicationIntakeDay d WHERE d.medication.id IN :medicationIds")
	void deleteAllByMedicationIds(@Param("medicationIds") List<Long> medicationIds);
}
