package server.yakssok.domain.medication.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication.domain.entity.MedicationIntakeTime;

@Repository
public interface MedicationIntakeTimeRepository extends JpaRepository<MedicationIntakeTime, Long> {
}
