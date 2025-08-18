package server.yakssok.global.common.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import server.yakssok.global.exception.ErrorCode;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

@Component
public class ApiErrorResponseCustomizer implements OperationCustomizer {
	@Override
	public Operation customize(Operation operation, HandlerMethod handlerMethod) {
		List<ApiErrorResponse> errorAnnotations = new ArrayList<>();

		ApiErrorResponse single = handlerMethod.getMethodAnnotation(ApiErrorResponse.class);
		if (single != null) errorAnnotations.add(single);

		ApiErrorResponses multiple = handlerMethod.getMethodAnnotation(ApiErrorResponses.class);
		if (multiple != null) errorAnnotations.addAll(Arrays.asList(multiple.value()));

		Map<String, ApiResponse> statusToApiResponse = new HashMap<>();

		for (ApiErrorResponse error : errorAnnotations) {
			ErrorCode errorCode = error.value();
			String status = String.valueOf(errorCode.getHttpStatus());

			// 예시 데이터
			Map<String, Object> exampleValue = new HashMap<>();
			exampleValue.put("code", errorCode.getCode());
			exampleValue.put("message", errorCode.getMessage());
			exampleValue.put("body", new HashMap<>());

			// ApiResponse를 status별로 관리
			ApiResponse apiResponse = statusToApiResponse.get(status);
			if (apiResponse == null) {
				apiResponse = new ApiResponse()
					.description("에러 응답"); // 여러 에러라면 통합 메시지 사용
				// 기본 Content 및 MediaType 세팅
				Content content = new Content();
				MediaType mediaType = new MediaType()
					.schema(new Schema<>().$ref("#/components/schemas/ApiResponse"));
				content.addMediaType("application/json", mediaType);
				apiResponse.setContent(content);
				statusToApiResponse.put(status, apiResponse);
			}

			// 여러 예시 등록 (code 이름으로 구분)
			MediaType mediaType = apiResponse.getContent().get("application/json");
			if (mediaType.getExamples() == null) {
				mediaType.setExamples(new HashMap<>());
			}
			Example example = new Example().value(exampleValue).summary(errorCode.getMessage());
			mediaType.getExamples().put(errorCode.name(), example);
		}

		for (Map.Entry<String, ApiResponse> entry : statusToApiResponse.entrySet()) {
			operation.getResponses().addApiResponse(entry.getKey(), entry.getValue());
		}

		return operation;
	}
}