package server.yakssok.global.infra.oauth.apple;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AppleJwksConfig {

	private final AppleOAuthProperties appleProps;

	@Bean
	public JwkProvider appleJwkProvider() {
		try {
			return new JwkProviderBuilder(new URL(appleProps.jwkUrl()))
				.cached(10, 6, TimeUnit.HOURS)     // ✅ 캐시(키 10개, 6시간)
				.build();
		} catch (MalformedURLException e) {
			throw new IllegalStateException("Invalid oauth.apple.jwk-url: " + appleProps.jwkUrl(), e);
		}
	}
}