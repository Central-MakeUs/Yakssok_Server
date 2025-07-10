package server.yakssok.domain.medication.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication.domain.entity.MedicationIntakeDay;

@Repository
public interface MedicationIntakeDayRepository extends JpaRepository<MedicationIntakeDay, Long> {
}
