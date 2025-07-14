package com.odit.backend.global.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.odit.backend.global.config.property.AwsProperties;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@EnableConfigurationProperties(AwsProperties.class)
public class S3Config {

	private final AwsProperties awsProperties;

	private StaticCredentialsProvider createCredentialsProvider() {
		AwsCredentials credentials = AwsBasicCredentials.create(
			awsProperties.credentials().accessKey(),
			awsProperties.credentials().secretKey()
		);
		return StaticCredentialsProvider.create(credentials);
	}

	@Bean
	public S3Client s3Client() {
		return S3Client.builder()
			.region(Region.of(awsProperties.region().static_()))
			.credentialsProvider(createCredentialsProvider())
			.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		return S3Presigner.builder()
			.credentialsProvider(createCredentialsProvider())
			.region(Region.of(awsProperties.region().static_()))
			.build();
	}
}