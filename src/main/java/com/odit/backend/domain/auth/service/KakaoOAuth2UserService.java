package com.odit.backend.domain.auth.service;

import java.io.IOException;
import java.util.Map;

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
public class KakaoOAuth2UserService extends AbstractOAuth2UserService {

	protected KakaoOAuth2UserService(JwtTokenProvider jwtTokenProvider,
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
		Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		String email = (String)kakaoAccount.get("email");
		String name = (String)profile.get("nickname");
		String profileImage = (String)profile.get("profile_image_url");

		OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.builder()
			.name(name)
			.email(email)
			.profile(profileImage)
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

		String redirectUrl = determineRedirectUrl();
		log.debug("[Kakao OAuth2] 리다이렉션 URL: {}", redirectUrl);
		response.sendRedirect(redirectUrl);
	}
}
