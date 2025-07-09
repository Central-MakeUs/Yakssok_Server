package server.yakssok.domain.medication.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationService;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequest;
import server.yakssok.domain.medication.presentation.dto.response.MedicationCardResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.reponse.PageResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/medications")
@Tag(name = "Medication", description = "복약 API")
public class MedicationController {
	private final MedicationService medicationService;

	@Operation(summary = "복약 루틴 등록")
	@ApiErrorResponse(value = ErrorCode.INVALID_INPUT_VALUE)
	@PostMapping
	public ApiResponse createMedication(
		@Valid @RequestBody CreateMedicationRequest createMedicationRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		medicationService.createMedication(userId, createMedicationRequest);
		return ApiResponse.success();
	}

	@Operation(summary = "전체 복약 루틴 목록 조회")
	@GetMapping
	public ApiResponse<PageResponse<MedicationCardResponse>> getMedications(
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		return ApiResponse.success(medicationService.findMedications(userId));
	}
}
