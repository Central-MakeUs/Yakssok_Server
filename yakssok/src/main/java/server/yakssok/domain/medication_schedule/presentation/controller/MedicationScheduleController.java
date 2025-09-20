package server.yakssok.domain.medication_schedule.presentation.controller;

import static server.yakssok.global.exception.ErrorCode.*;

import java.time.LocalDate;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication_schedule.application.service.MedicationScheduleService;
import server.yakssok.domain.medication_schedule.batch.job.MedicationScheduleJob;
import server.yakssok.domain.medication_schedule.presentation.dto.response.MedicationScheduleGroupResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medication-schedules")
@Tag(name = "Medication Schedule", description = "복약 스케줄 API")
public class MedicationScheduleController {
	private final MedicationScheduleService medicationScheduleService;
	private final MedicationScheduleJob job;

	@Operation(summary = "나의 복약 스케줄 조회 (오늘)")
	@GetMapping("/today")
	public ApiResponse<MedicationScheduleGroupResponse> findMyTodayMedicationSchedule(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(medicationScheduleService.getMyTodaySchedules(userId));
	}

	@Operation(summary = "나의 복약 스케줄 조회 (기간)")
	@GetMapping
	public ApiResponse<MedicationScheduleGroupResponse> findMyRangeMedicationSchedule(
		@Parameter(description = "시작일 (YYYY-MM-DD)", required = true)
		@RequestParam LocalDate startDate,
		@Parameter(description = "종료일 (YYYY-MM-DD)", required = true)
		@RequestParam LocalDate endDate,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(medicationScheduleService.getMyRangeSchedules(userId, startDate, endDate));
	}

	@Operation(summary = "복약 스케줄 복용/미복용 처리")
	@ApiErrorResponses(value = {
		@ApiErrorResponse(value = NOT_FOUND_MEDICATION_SCHEDULE),
		@ApiErrorResponse(value = FORBIDDEN),
		@ApiErrorResponse(ErrorCode.NOT_TODAY_SCHEDULE)
	})
	@PutMapping("/{scheduleId}/take")
	public ApiResponse switchTakeMedication(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long scheduleId
	) {
		Long userId = userDetails.getUserId();
		medicationScheduleService.switchTakeMedication(userId, scheduleId);
		return ApiResponse.success();
	}

	@Operation(summary = "지인 복약 스케줄 조회 (오늘)")
	@GetMapping("/friends/{friendId}/today")
	public ApiResponse<MedicationScheduleGroupResponse> findFriendTodayMedicationSchedule(
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long friendId
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(medicationScheduleService.getFriendTodaySchedules(userId, friendId));
	}

	@Operation(summary = "지인 복약 스케줄 조회 (기간)")
	@GetMapping("/friends/{friendId}")
	public ApiResponse<MedicationScheduleGroupResponse> findFriendRangeMedicationSchedule(
		@Parameter(description = "시작일 (YYYY-MM-DD)", required = true)
		@RequestParam LocalDate startDate,
		@Parameter(description = "종료일 (YYYY-MM-DD)", required = true)
		@RequestParam LocalDate endDate,
		@AuthenticationPrincipal YakssokUserDetails userDetails,
		@PathVariable Long friendId
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(medicationScheduleService.getFriendRangeSchedules(userId, friendId, startDate, endDate));
	}
}
