package server.yakssok.global.common.swagger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;

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

		for (ApiErrorResponse error : errorAnnotations) {
			Map<String, Object> example = new HashMap<>();
			example.put("code", error.code());
			example.put("message", error.message());
			example.put("body", new HashMap<>());

			ApiResponse apiResponse = new ApiResponse()
				.description(error.message())
				.content(new Content().addMediaType("application/json",
					new MediaType()
						.schema(new Schema<>().$ref("#/components/schemas/ApiResponse"))
						.example(example)));

			operation.getResponses().addApiResponse(String.valueOf(error.httpStatus()), apiResponse);
		}
		return operation;
	}
}