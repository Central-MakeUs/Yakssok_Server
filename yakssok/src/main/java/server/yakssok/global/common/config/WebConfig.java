package server.yakssok.global.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("https://*", "http://*") // 모든 도메인 + 프로토콜
			.allowedMethods("*")
			.allowedHeaders("*")
			.allowCredentials(true);
	}
}
