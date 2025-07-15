package server.yakssok.domain.medication_schedule.presentation.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "복용일별 복약 스케줄 그룹핑 응답")
public record MedicationScheduleGroupResponse(
	Map<LocalDate, List<MedicationScheduleResponse>> groupedSchedules
) {
	public static MedicationScheduleGroupResponse of(
		Map<LocalDate, List<MedicationScheduleResponse>> groupedSchedules) {
		return new MedicationScheduleGroupResponse(groupedSchedules);
	}

	public static MedicationScheduleGroupResponse fromList(List<MedicationScheduleResponse> list) {
		Map<LocalDate, List<MedicationScheduleResponse>> map =
			list.stream().collect(Collectors.groupingBy(
				r -> r.date()
			));
		Map<LocalDate, List<MedicationScheduleResponse>> sortedMap = new TreeMap<>(map);
		return new MedicationScheduleGroupResponse(sortedMap);
	}
}