package server.yakssok.domain.medication.presentation.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.medication.application.service.MedicationServiceV2;
import server.yakssok.domain.medication.presentation.dto.request.CreateMedicationRequestV2;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.security.YakssokUserDetails;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.exception.ErrorCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/medications")
@Tag(name = "Medication", description = "복약 API v2")
public class MedicationControllerV2 {
	private final MedicationServiceV2 medicationServiceV2;

	@Operation(summary = "복약 루틴 등록 (종료일 필수)")
	@ApiErrorResponse(value = ErrorCode.INVALID_INPUT_VALUE)
	@PostMapping
	public ApiResponse createMedicationV2(
		@RequestBody @Valid CreateMedicationRequestV2 createMedicationRequest,
		@AuthenticationPrincipal YakssokUserDetails userDetails
	) {
		Long userId = userDetails.getUserId();
		medicationServiceV2.createMedication(userId, createMedicationRequest);
		return ApiResponse.success();
	}
}
