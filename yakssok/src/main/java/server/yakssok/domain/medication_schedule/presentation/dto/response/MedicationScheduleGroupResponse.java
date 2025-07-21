package server.yakssok.domain.medication_schedule.presentation.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "복용일별 복약 스케줄 그룹핑 응답")
public record MedicationScheduleGroupResponse(
	Map<LocalDate, List<DailyMedicationScheduleGroupResponse>> groupedSchedules
) {
	public static MedicationScheduleGroupResponse of(
		Map<LocalDate, List<DailyMedicationScheduleGroupResponse>> groupedSchedules) {
		return new MedicationScheduleGroupResponse(groupedSchedules);
	}

	public static MedicationScheduleGroupResponse fromList(List<DailyMedicationScheduleGroupResponse> list) {
		Map<LocalDate, List<DailyMedicationScheduleGroupResponse>> map =
			list.stream().collect(Collectors.groupingBy(DailyMedicationScheduleGroupResponse::date));
		Map<LocalDate, List<DailyMedicationScheduleGroupResponse>> sortedMap = new TreeMap<>(map);
		return new MedicationScheduleGroupResponse(sortedMap);
	}
}