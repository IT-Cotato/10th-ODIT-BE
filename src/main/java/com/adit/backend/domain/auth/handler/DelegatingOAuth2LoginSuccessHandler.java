package com.adit.backend.domain.auth.handler;

import static com.adit.backend.global.error.GlobalErrorCode.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.adit.backend.domain.auth.exception.AuthException;
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
			throw new AuthException(INVALID_AUTHENTICATION_TYPE);
		}
		String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();
		AuthenticationSuccessHandler delegate = Optional.ofNullable(handlerMap)
			.map(v -> v.get(registrationId))
			.orElseThrow(() -> new AuthException(PROVIDER_NOT_FOUND));

		delegate.onAuthenticationSuccess(request, response, authentication);
	}
}
