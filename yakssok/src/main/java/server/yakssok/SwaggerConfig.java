package server.yakssok;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
			.info(new Info()
				.title("약:쏙 API 문서")
				.description("약쏙 백엔드 Swagger 문서입니다.")
				.version("v1.0.0")
			);
	}
}
