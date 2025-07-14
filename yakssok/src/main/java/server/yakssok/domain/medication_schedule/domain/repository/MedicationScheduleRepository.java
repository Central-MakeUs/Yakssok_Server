package server.yakssok.domain.medication_schedule.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication_schedule.domain.entity.MedicationSchedule;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, Long>, MedicationScheduleQueryRepository {
}
