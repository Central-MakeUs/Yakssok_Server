package server.yakssok.domain.medication.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import server.yakssok.domain.medication.domain.entity.Medication;
@Repository
public interface MedicationRepository extends JpaRepository<Medication, Long>, MedicationQueryRepository{
	boolean existsByUserId(Long userId);
}
