package server.yakssok.domain.medication.domain.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;

public interface MedicationQueryRepository {
	List<Medication> findAllUserMedications(Long userId);
	List<MedicationScheduleDto> findMedicationsByDate(LocalDate date, DayOfWeek dayOfWeek);
}
