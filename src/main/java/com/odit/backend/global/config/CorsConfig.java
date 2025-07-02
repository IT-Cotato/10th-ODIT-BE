package com.odit.backend.global.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

	private static final String SERVER_URL = "https://odit.store";
	private static final String FRONT_URL = "https://odit-lilac.vercel.app";
	private static final String FRONT_LOCALHOST_URL = "http://localhost:3000";
	private static final String SERVER_LOCALHOST_URL = "http://localhost:8080";

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin(SERVER_URL);
		config.addAllowedOrigin(FRONT_URL);
		config.addAllowedOrigin(FRONT_LOCALHOST_URL);
		config.addAllowedOrigin(SERVER_LOCALHOST_URL);
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setExposedHeaders(List.of(
			"accessToken",
			"Authorization",
			"Authorization-refresh"
		));
		config.addExposedHeader("Set-Cookie");

		// 모든 엔드포인트에 대해 CORS 설정 적용
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}
}

