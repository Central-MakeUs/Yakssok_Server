package server.yakssok.global.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
	private final SwaggerProperties swaggerProperties;

	@Bean
	public OpenAPI openAPI() {
		final String securitySchemeName = "bearerAuth";

		return new OpenAPI()
			.addServersItem(new Server().url(swaggerProperties.serverUrl()))
			.info(new Info()
				.title("Yakssok API")
				.description("약속 API 문서")
				.version("v1"))
			.addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
			.components(new io.swagger.v3.oas.models.Components()
				.addSecuritySchemes(securitySchemeName,
					new SecurityScheme()
						.name(securitySchemeName)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));
	}

	@Bean
	public ApiErrorResponseCustomizer apiErrorResponseCustomizer() {
		return new ApiErrorResponseCustomizer();
	}
}
