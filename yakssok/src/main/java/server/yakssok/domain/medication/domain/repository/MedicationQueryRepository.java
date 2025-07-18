package server.yakssok.domain.medication.domain.repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;

import server.yakssok.domain.medication.domain.entity.Medication;
import server.yakssok.domain.medication.domain.repository.dto.FutureMedicationSchedulesDto;
import server.yakssok.domain.medication.domain.repository.dto.MedicationDto;

public interface MedicationQueryRepository {
	List<Medication> findAllUserMedications(Long userId);
	List<MedicationDto> findMedicationsForScheduleGeneration(LocalDateTime dateTime, DayOfWeek dayOfWeek);
	List<FutureMedicationSchedulesDto> findFutureMedicationSchedules(Long userId);
}
