package server.yakssok.domain.medication_schedule.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleGroupResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-schedules")
public class MedicationScheduleController {
	private final MedicationScheduleService medicationScheduleService;

	@GetMapping
	public ApiResponse<TodayMedicationScheduleGroupResponse> getMedicationSchedule(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		TodayMedicationScheduleGroupResponse todayMedicationScheduleGroup = medicationScheduleService.findTodayMedicationSchedule(
			userId);
		return ApiResponse.success(todayMedicationScheduleGroup);
	}
}
