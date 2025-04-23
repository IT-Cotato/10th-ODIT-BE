package com.adit.backend.domain.auth.handler;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.adit.backend.domain.auth.service.GoogleOAuth2UserService;
import com.adit.backend.domain.auth.service.KakaoOAuth2UserService;
import com.adit.backend.domain.auth.service.NaverOAuth2UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class DelegatingOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final Map<String, AuthenticationSuccessHandler> handlerMap;

	public DelegatingOAuth2LoginSuccessHandler(
		GoogleOAuth2UserService googleHandler,
		NaverOAuth2UserService naverHandler,
		KakaoOAuth2UserService kakaoHandler
	) {
		this.handlerMap = Map.of(
			"google", googleHandler,
			"naver", naverHandler,
			"kakao", kakaoHandler
		);
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException {
		if (!(authentication instanceof OAuth2AuthenticationToken oAuth2Token)) {
			throw new IllegalArgumentException("OAuth2AuthenticationToken required");
		}
		String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();
		AuthenticationSuccessHandler delegate = handlerMap.get(registrationId);

		if (delegate == null) {
			throw new IllegalArgumentException("No handler for provider: " + registrationId);
		}
		delegate.onAuthenticationSuccess(request, response, authentication);
	}
}
