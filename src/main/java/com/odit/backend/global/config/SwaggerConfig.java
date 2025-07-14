package com.odit.backend.global.config;

import java.util.Arrays;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI openAPI() {
        		SecurityScheme accessTokenAuth = new SecurityScheme()
			.type(SecurityScheme.Type.HTTP)
			.scheme("bearer")
			.bearerFormat("JWT")
			.in(SecurityScheme.In.HEADER)
			.name("Authorization");

		SecurityScheme refreshTokenAuth = new SecurityScheme()
			.type(SecurityScheme.Type.APIKEY)
			.in(SecurityScheme.In.COOKIE)
			.name("refreshToken");

		SecurityRequirement securityRequirement = new SecurityRequirement()
			.addList("accessTokenAuth")
			.addList("refreshTokenAuth");

		Server httpsServer = new Server();
		httpsServer.setUrl("https://odit.store");
		httpsServer.setDescription("운영 서버");

		Server localServer = new Server();
		localServer.setUrl("http://localhost:8080");
		localServer.setDescription("로컬 서버");

		return new OpenAPI()
			.components(new Components()
				.addSecuritySchemes("accessTokenAuth", accessTokenAuth)
				.addSecuritySchemes("refreshTokenAuth", refreshTokenAuth))
			.security(Arrays.asList(securityRequirement))
			.info(new Info()
				.title("오딧(ODIT) api 명세서")
				.description("ODIT api 명세서입니다.")
				.version("1.0.0"))
			.servers(Arrays.asList(localServer, httpsServer)); // 서버 URL 추가
	}

	@Bean
	public GroupedOpenApi publicApi() {
		return GroupedOpenApi.builder()
			.group("public")
			.pathsToMatch("/api/**")
			.build();
	}
}
