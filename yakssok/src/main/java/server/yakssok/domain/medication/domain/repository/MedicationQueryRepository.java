package server.yakssok.domain.medication.domain.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;

public interface MedicationQueryRepository {
	List<Medication> findAllUserMedications(Long userId);
	List<MedicationDto> findMedicationsByDate(LocalDate date, DayOfWeek dayOfWeek);
}
