package com.odit.backend.domain.auth.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.odit.backend.domain.auth.dto.OAuth2UserInfo;
import com.odit.backend.domain.user.dto.response.UserResponse;
import com.odit.backend.domain.user.service.command.UserCommandService;
import com.odit.backend.global.security.jwt.entity.RefreshToken;
import com.odit.backend.global.security.jwt.entity.Token;
import com.odit.backend.global.security.jwt.repository.RefreshTokenRepository;
import com.odit.backend.global.security.jwt.util.JwtTokenProvider;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NaverOAuth2UserService extends AbstractOAuth2UserService {

	protected NaverOAuth2UserService(JwtTokenProvider jwtTokenProvider,
		RefreshTokenRepository refreshTokenRepository,
		UserCommandService userCommandService) {
		super(jwtTokenProvider, refreshTokenRepository, userCommandService);
	}

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

		String redirectUrl = determineRedirectUrl(request);
		log.debug("[Naver OAuth2] 리다이렉션 URL: {}", redirectUrl);
		response.sendRedirect(redirectUrl);
	}
}
