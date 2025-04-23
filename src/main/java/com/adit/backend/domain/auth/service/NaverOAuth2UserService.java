package com.adit.backend.domain.auth.service;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import com.adit.backend.domain.auth.dto.OAuth2UserInfo;
import com.adit.backend.domain.user.dto.response.UserResponse;
import com.adit.backend.domain.user.service.command.UserCommandService;
import com.adit.backend.global.security.jwt.entity.RefreshToken;
import com.adit.backend.global.security.jwt.entity.Token;
import com.adit.backend.global.security.jwt.repository.RefreshTokenRepository;
import com.adit.backend.global.security.jwt.util.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class NaverOAuth2UserService extends SimpleUrlAuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenRepository refreshTokenRepository;
	private final UserCommandService userCommandService;

	@Value("${token.access.header}")
	private String accessTokenHeader;
	@Value("${token.refresh.expiration}")
	private String refreshTokenExpiresAt;

	@Value("${token.refresh.cookie.name}")
	private String refreshTokenCookieName;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws
		IOException,
		ServletException {

		//oauth 프로필 추출
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String name = oAuth2User.getAttribute("name");
		String profile = oAuth2User.getAttribute("profile_image");
		String email = oAuth2User.getAttribute("email");
		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
			.name(name)
			.email(email)
			.profile(profile)
			.build();
		UserResponse.InfoDto infoDto = userCommandService.createOrUpdateUser(oAuth2UserInfo);

		//jwt 토큰 생성
		Token jwtToken = jwtTokenProvider.createToken(infoDto.Id(), infoDto.role());
		String newAccessToken = jwtToken.getAccessToken();
		String newRefreshToken = jwtToken.getRefreshToken();

		authentication = jwtTokenProvider.getAuthentication(newAccessToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		response.setHeader(accessTokenHeader, "Bearer " + newAccessToken);

		RefreshToken refreshToken = new RefreshToken(infoDto.Id(), newRefreshToken);
		refreshTokenRepository.save(refreshToken);
		addRefreshTokenToCookie(newRefreshToken, response);

		response.sendRedirect("http://localhost:5173");

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
