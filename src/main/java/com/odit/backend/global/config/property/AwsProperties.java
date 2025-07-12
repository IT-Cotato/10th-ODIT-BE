package com.odit.backend.global.config.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ConfigurationProperties("spring.cloud.aws")
public record AwsProperties(
	@NotNull
	Credentials credentials,
	@NotNull
	Region region,
	@NotNull
	S3 s3
) {
	public record Credentials(
		@NotBlank
		String accessKey,
		@NotBlank
		String secretKey
	) {
	}

	public record Region(
		@NotBlank
		String static_
	) {
	}

	public record S3(
		@NotBlank
		String bucket
	) {
	}
}