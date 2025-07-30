package server.yakssok.global.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import server.yakssok.global.common.jwt.JwtAuthService;
import server.yakssok.global.common.jwt.JwtAuthenticationEntryPoint;
import server.yakssok.global.common.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthService jwtAuthService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	static final String[] PERMIT_URLS = {
		"/v3/api-docs/**",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/api/auth/join/**",
		"/api/auth/login/**",
		"/api/auth/reissue/**",
		"/api/auth/kakao/callback",
		"/actuator/**"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.sessionManagement(session -> session
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 사용 X

			.exceptionHandling(exception -> exception
				.authenticationEntryPoint(jwtAuthenticationEntryPoint) // 여기 추가
			)

			.authorizeHttpRequests(auth -> auth
				.requestMatchers(PERMIT_URLS).permitAll()
				.anyRequest().authenticated()
			)

			.addFilterBefore(new JwtAuthenticationFilter(jwtAuthService),
				UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

}