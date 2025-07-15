package server.yakssok.domain.medication_schedule.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.domain.medication_schedule.presentation.dto.MedicationScheduleGroupResponse;
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

	@Operation(summary = "나의 복약 스케줄 조회 (오늘)")
	@GetMapping("/today")
	public ApiResponse<MedicationScheduleGroupResponse> findTodayMedicationSchedule(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		MedicationScheduleGroupResponse medicationScheduleGroupResponse
			= medicationScheduleService.findTodayMedicationSchedule(userId);
		return ApiResponse.success(medicationScheduleGroupResponse);
	}

	@Operation(summary = "나의 복약 스케줄 조회 (기간)")
	@GetMapping
	public ApiResponse findRangeMedicationSchedule(
		@Parameter(description = "시작일 (YYYY-MM-DD)", required = true)
		@RequestParam String startDate,
		@Parameter(description = "종료일 (YYYY-MM-DD)", required = true)
		@RequestParam String endDate,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		MedicationScheduleGroupResponse rangeMedicationSchedule = medicationScheduleService.findRangeMedicationSchedule(
			userId, startDate, endDate);
		return ApiResponse.success(rangeMedicationSchedule);
	}

	@Operation(summary = "복약 스케줄 복용 처리")
	@ApiErrorResponse(value = ErrorCode.NOT_FOUND_MEDICATION_SCHEDULE)
	@PutMapping("/{scheduleId}/take")
	public ApiResponse takeMedication(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long scheduleId
	) {
		Long userId = userDetails.getUserId();
		medicationScheduleService.takeMedication(userId, scheduleId);
		return ApiResponse.success();
	}
}
