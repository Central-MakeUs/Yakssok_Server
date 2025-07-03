package server.yakssok.global.infra.oauth;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import server.yakssok.domain.user.domain.entity.OAuthType;

@Component
public class OAuthStrategyFactory {
	private final Map<OAuthType, OAuthStrategy> strategyMap;

	public OAuthStrategyFactory(List<OAuthStrategy> strategyList) {
		this.strategyMap = strategyList.stream()
			.collect(Collectors.toMap(OAuthStrategy::getOAuthType, Function.identity()));
	}
	public OAuthStrategy getStrategy(String oauthType) {
		OAuthStrategy strategy = strategyMap.get(OAuthType.from(oauthType));
		return strategy;
	}
}
