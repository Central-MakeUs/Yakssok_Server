package server.yakssok.domain.auth.application.client;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import server.yakssok.domain.auth.application.exception.AuthErrorCode;
import server.yakssok.domain.auth.application.exception.AuthException;
import server.yakssok.domain.user.domain.entity.Provider;

@Component
public class SocialLoginStrategyFactory {
	private final Map<Provider, SocialLoginStrategy> strategyMap;

	public SocialLoginStrategyFactory(List<SocialLoginStrategy> strategyList) {
		this.strategyMap = strategyList.stream()
			.collect(Collectors.toMap(SocialLoginStrategy::getSocialType, Function.identity()));
	}
	public SocialLoginStrategy getStrategy(String provider) {
		SocialLoginStrategy strategy = strategyMap.get(Provider.from(provider));
		if (strategy == null) {
			throw new AuthException(AuthErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
		}
		return strategy;
	}
}
