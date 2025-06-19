package com.odit.backend.domain.auth.service;

import static com.odit.backend.global.error.GlobalErrorCode.*;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.odit.backend.domain.auth.dto.response.ReissueResponse;
import com.odit.backend.domain.user.exception.UserException;
import com.odit.backend.domain.user.principal.PrincipalDetails;
import com.odit.backend.global.security.jwt.entity.RefreshToken;
import com.odit.backend.global.security.jwt.entity.Token;
import com.odit.backend.global.security.jwt.exception.TokenException;
import com.odit.backend.global.security.jwt.repository.BlackListRepository;
import com.odit.backend.global.security.jwt.repository.RefreshTokenRepository;
import com.odit.backend.global.security.jwt.service.JwtTokenService;
import com.odit.backend.global.security.jwt.util.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {
	private final JwtTokenProvider tokenProvider;
	private final JwtTokenService jwtTokenService;
	private final BlackListRepository blackListRepository;
	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${token.access.header}")
	private String accessTokenHeader;

	@Value("${token.refresh.expiration}")
	private String refreshTokenExpiresAt;

	@Value("${token.refresh.cookie.name}")
	private String refreshTokenCookieName;

	// 토큰 재발급
	public ReissueResponse reIssue(String refreshToken, HttpServletResponse response) {
		if (!tokenProvider.isRefreshTokenValid(refreshToken) || blackListRepository.existsById(refreshToken)) {
			log.warn("[Token] 블랙리스트 토큰 재사용 시도 - blacklisted: {}", blackListRepository.existsById(refreshToken));
			throw new TokenException(REFRESH_TOKEN_BLACKLISTED);
		}

		Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
		PrincipalDetails userDetails = tokenProvider.getUserDetails(authentication);
		RefreshToken findToken = refreshTokenRepository.findById(userDetails.getUser().getId())
			.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));

		log.debug("[Token] 리프레시 토큰 검증 - isValid: {}", refreshToken.equals(findToken.getRefreshToken()));
		if (!refreshToken.equals(findToken.getRefreshToken())) {
			log.error("[Token] 리프레시 토큰 불일치 발생 - expected: {}, actual: {}", findToken.getRefreshToken(), refreshToken);
			throw new TokenException(REFRESH_TOKEN_MISMATCH);
		}

		jwtTokenService.setBlackList(refreshToken);
		Token token = tokenProvider.createToken(userDetails.getUser().getId(), userDetails.getUser().getRole());

		findToken.updateRefreshToken(token.getRefreshToken());
		refreshTokenRepository.save(findToken);

		addRefreshTokenToCookie(token.getRefreshToken(), response);
		log.info("[Token] 토큰 재발급 완료 - userId: {}", userDetails.getUser().getId());
		return ReissueResponse.from(token.getAccessToken());
	}

	// 토큰 삭제 및 로그아웃
	public void logout(String refreshToken, HttpServletResponse response) {
		try {
			Authentication authentication = tokenProvider.getAuthenticationFromRefreshToken(refreshToken);
			PrincipalDetails userDetails = tokenProvider.getUserDetails(authentication);
			log.debug("[Auth] 로그아웃 시작 - userId: {}", userDetails.getUser().getId());

			RefreshToken existRefreshToken = refreshTokenRepository.findById(userDetails.getUser().getId())
				.orElseThrow(() -> new TokenException(TOKEN_NOT_FOUND));
			jwtTokenService.setBlackList(refreshToken);
			refreshTokenRepository.delete(existRefreshToken);
			addRefreshTokenToCookie(null, response);

			log.info("[Auth] 로그아웃 완료 - userId: {}", userDetails.getUser().getId());
		} catch (Exception e) {
			log.error("[Auth] 로그아웃 실패: {}", e.getMessage());
			throw new UserException(LOGOUT_FAILED);
		}
	}

	private void addRefreshTokenToCookie(String refreshToken, HttpServletResponse response) {
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
}
