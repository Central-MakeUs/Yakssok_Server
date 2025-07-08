package server.yakssok.domain.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.image.service.ImageService;
import server.yakssok.domain.image.dto.UploadImageResponse;
import server.yakssok.global.ApiResponse;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @Operation(summary = "이미지 업로드")
    @ApiErrorResponses(
        value = {
            @ApiErrorResponse(ErrorCode.FAILED_FILE_UPLOAD),
            @ApiErrorResponse(ErrorCode.UNSUPPORTED_FILE_TYPE)
        }
    )
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<UploadImageResponse> uploadImages(
        @RequestPart MultipartFile file,
        @Parameter(example = "profile") @RequestParam("type") String type
    ) {
        return ApiResponse.success(imageService.upload(file, type));
    }


    @Operation(summary = "이미지 수정 (기존 삭제 + 새 업로드)")
    @ApiErrorResponses({
        @ApiErrorResponse(ErrorCode.FAILED_FILE_DELETE),
        @ApiErrorResponse(ErrorCode.FAILED_FILE_UPLOAD),
        @ApiErrorResponse(ErrorCode.UNSUPPORTED_FILE_TYPE)
    })
    @PutMapping(consumes = "multipart/form-data")
    public ApiResponse<UploadImageResponse> updateImage(
        @RequestPart(name = "file") MultipartFile file,
        @Parameter(example = "profile") @RequestParam(name = "type") String type,
        @RequestParam(name = "oldImageUrl") String oldImageUrl
    ) {
        return ApiResponse.success(imageService.update(file, type, oldImageUrl));
    }

    @Operation(summary = "이미지 삭제")
    @ApiErrorResponse(value = ErrorCode.FAILED_FILE_DELETE)
    @DeleteMapping
    public ApiResponse deleteImages(@RequestParam String imageUrl) {
        imageService.delete(imageUrl);
        return ApiResponse.success();
    }
}
