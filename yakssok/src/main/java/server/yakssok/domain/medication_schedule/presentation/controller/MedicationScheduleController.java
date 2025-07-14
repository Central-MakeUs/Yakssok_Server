package server.yakssok.domain.medication_schedule.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.domain.medication_schedule.presentation.dto.TodayMedicationScheduleGroupResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-schedules")
@Tag(name = "Medication Schedule", description = "복약 스케줄 API")
public class MedicationScheduleController {
	private final MedicationScheduleService medicationScheduleService;

	@Operation(summary = "오늘 복약 스케줄 조회")
	@GetMapping
	public ApiResponse<TodayMedicationScheduleGroupResponse> getMedicationSchedule(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		TodayMedicationScheduleGroupResponse todayMedicationScheduleGroup = medicationScheduleService.findTodayMedicationSchedule(
			userId);
		return ApiResponse.success(todayMedicationScheduleGroup);
	}

	@Operation(summary = "복약 스케줄 복용 처리")
	@ApiErrorResponse(value = ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE)
	@PutMapping("/{scheduleId}/take")
	public ApiResponse takeMedication(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long scheduleId
	) {
		Long userId = userDetails.getUserId();
		medicationScheduleService.takeMedication(userId, scheduleId); // 서비스에서 권한 등 체크
		return ApiResponse.success();
	}
}
