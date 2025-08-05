package server.yakssok.domain.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import server.yakssok.domain.image.service.ImageService;
import server.yakssok.domain.image.dto.UploadImageResponse;
import server.yakssok.global.common.reponse.ApiResponse;
import server.yakssok.global.common.swagger.ApiErrorResponse;
import server.yakssok.global.common.swagger.ApiErrorResponses;
import server.yakssok.global.exception.ErrorCode;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Image", description = "이미지 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {
    private final ImageService imageService;

    @Operation(
        summary = "이미지 업로드",
        description = """
            [허용 확장자]: .jpg, .jpeg, .png, .gif, .bmp, .webp<br>
            [최대 용량]: 1MB<br>
            [제한]: 위 확장자가 아닌 파일, 또는 1MB 초과 파일 업로드 시 오류 반환<br>
            """
    )
    @ApiErrorResponses(
        value = {
            @ApiErrorResponse(ErrorCode.FAILED_FILE_UPLOAD),
            @ApiErrorResponse(ErrorCode.UNSUPPORTED_FILE_TYPE),
            @ApiErrorResponse(ErrorCode.INVALID_FILE_EXTENSION)
        }
    )
    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<UploadImageResponse> uploadImages(
        @RequestPart MultipartFile file,
        @Parameter(example = "profile") @RequestParam("type") String type
    ) {
        return ApiResponse.success(imageService.upload(file, type));
    }

    @Operation(
        summary = "이미지 수정 (기존 삭제 + 새 업로드)",
        description = """
            [허용 확장자]: .jpg, .jpeg, .png, .gif, .bmp, .webp<br>
            [최대 용량]: 1MB<br>
            [제한]: 위 확장자가 아닌 파일, 또는 1MB 초과 파일 업로드 시 오류 반환<br>
            """
    )
    @ApiErrorResponses({
        @ApiErrorResponse(ErrorCode.FAILED_FILE_DELETE),
        @ApiErrorResponse(ErrorCode.FAILED_FILE_UPLOAD),
        @ApiErrorResponse(ErrorCode.UNSUPPORTED_FILE_TYPE),
        @ApiErrorResponse(ErrorCode.INVALID_FILE_EXTENSION)
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
