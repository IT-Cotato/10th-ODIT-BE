package com.adit.backend.domain.auth.service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import com.adit.backend.domain.user.service.command.UserCommandService;
import com.adit.backend.global.security.jwt.repository.RefreshTokenRepository;
import com.adit.backend.global.security.jwt.util.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public abstract class AbstractOAuth2UserService extends SimpleUrlAuthenticationSuccessHandler {
	protected static final String FRONT_REDIRECT_URI = "http://localhost:3000/";
	private static final String FRONT_PORT = "3000";
	protected final JwtTokenProvider jwtTokenProvider;
	protected final RefreshTokenRepository refreshTokenRepository;
	protected final UserCommandService userCommandService;
	@Value("${token.access.header}")
	protected String accessTokenHeader;
	@Value("${token.refresh.expiration}")
	protected String refreshTokenExpiresAt;
	@Value("${token.refresh.cookie.name}")
	protected String refreshTokenCookieName;
	@Value("${base-url}")
	protected String baseUrl;

	@Override
	public abstract void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException, ServletException;

	public void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
		Cookie cookie = new Cookie(refreshTokenCookieName, refreshToken);

		if (cookie.getValue() == null) {
			cookie.setMaxAge(0);
			log.debug("[Token] 리프레시 토큰 쿠키 삭제");
		}
		cookie.setPath("/");
		ZonedDateTime seoulTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
		ZonedDateTime expirationTime = seoulTime.plusSeconds(Long.parseLong(refreshTokenExpiresAt));
		cookie.setMaxAge((int)(expirationTime.toEpochSecond() - seoulTime.toEpochSecond()));
		cookie.setSecure(true);
		cookie.setHttpOnly(true);
		response.addCookie(cookie);
		log.debug("[Token] 리프레시 토큰 쿠키 생성 - name: {}, maxAge: {}", refreshTokenCookieName, cookie.getMaxAge());
	}

	protected String determineRedirectUrl(HttpServletRequest request) {
		if (request.getHeader("Referer") != null && request.getHeader("Referer").contains(FRONT_PORT)) {
			return FRONT_REDIRECT_URI;
		} else {
			return baseUrl;
		}
	}
}
