package com.odit.backend.infra.s3.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.odit.backend.global.config.property.AwsProperties;
import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.infra.s3.exception.AwsException;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsS3Service {

	private final S3Template s3Template;
	private final Executor imageUploadExecutor;
	private final AwsProperties awsProperties;

	private static final String DATE_FORMAT = "yyyy/MM/dd";
	private static final int CONNECTION_TIMEOUT = 5000;
	private static final int READ_TIMEOUT = 10000;
	private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	/**
	 * 이미지 URL 목록을 S3에 비동기로 업로드
	 *
	 * @param imageUrlList 업로드할 이미지 URL 목록
	 * @param dirName      저장할 디렉토리 이름
	 * @return 업로드된 파일들의 S3 URL 목록
	 */
	@Async("imageUploadExecutor")
	public CompletableFuture<List<String>> uploadImageFromUrls(List<String> imageUrlList, String dirName) {
		log.info("[S3] 이미지 URL 업로드 시작 - 총 {}개 파일, 디렉토리: {}", imageUrlList.size(), dirName);

		List<CompletableFuture<String>> uploadFutures = IntStream.range(0, imageUrlList.size())
			.mapToObj(index -> CompletableFuture.supplyAsync(() -> {
				String imageUrl = imageUrlList.get(index);
				try {
					return uploadSingleImageFromUrl(imageUrl, dirName, index);
				} catch (Exception e) {
					log.error("[S3] 이미지 업로드 실패 - URL: {}, 에러: {}", imageUrl, e.getMessage(), e);
					throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
				}
			}, imageUploadExecutor))
			.toList();

		return CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]))
			.thenApply(v -> uploadFutures.stream()
				.map(CompletableFuture::join)
				.toList())
			.whenComplete((result, throwable) -> {
				if (throwable != null) {
					log.error("[S3] 이미지 업로드 배치 실패", throwable);
				} else {
					log.info("[S3] 이미지 업로드 배치 완료 - 성공: {}개", result.size());
				}
			});
	}

	/**
	 * MultipartFile을 S3에 업로드
	 *
	 * @param fileList 업로드할 파일 목록
	 * @param dirName  저장할 디렉토리 이름
	 * @return 업로드된 파일들의 S3 URL 목록
	 */
	public CompletableFuture<List<String>> uploadImageFromFile(List<MultipartFile> fileList, String dirName) {
		log.info("[S3] 파일 업로드 시작 - 총 {}개 파일, 디렉토리: {}", fileList.size(), dirName);

		List<CompletableFuture<String>> uploadFutures = fileList.stream()
			.map(multipartFile -> CompletableFuture.supplyAsync(() -> {
				try {
					return uploadSingleFile(multipartFile, dirName);
				} catch (Exception e) {
					log.error("[S3] 파일 업로드 실패 - 파일명: {}, 에러: {}", multipartFile.getOriginalFilename(), e.getMessage(),
						e);
					throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
				}
			}, imageUploadExecutor))
			.toList();

		return CompletableFuture.allOf(uploadFutures.toArray(new CompletableFuture[0]))
			.thenApply(v -> uploadFutures.stream()
				.map(CompletableFuture::join)
				.toList())
			.whenComplete((result, throwable) -> {
				if (throwable != null) {
					log.error("[S3] 파일 업로드 배치 실패", throwable);
				} else {
					log.info("[S3] 파일 업로드 배치 완료 - 성공: {}개", result.size());
				}
			});
	}

	/**
	 * S3에서 파일 삭제
	 *
	 * @param s3Url 삭제할 파일의 S3 URL
	 */
	public void deleteFile(String s3Url) {
		if (!StringUtils.hasText(s3Url)) {
			log.warn("[S3] 삭제할 파일 URL이 비어있습니다.");
			return;
		}

		try {
			String key = extractKeyFromUrl(s3Url);
			log.info("[S3] 파일 삭제 시작 - 키: {}", key);

			s3Template.deleteObject(awsProperties.s3().bucket(), key);

			log.info("[S3] 파일 삭제 완료 - 키: {}", key);

		} catch (Exception e) {
			log.error("[S3] 파일 삭제 실패 - URL: {}, 에러: {}", s3Url, e.getMessage(), e);
			throw new AwsException(GlobalErrorCode.S3_DELETE_FAILED);

		}
	}

	/**
	 * 단일 이미지 URL을 S3에 업로드
	 */
	private String uploadSingleImageFromUrl(String imageUrl, String dirName, int index) {
		try {
			String normalizedUrl = normalizeUrl(imageUrl);
			byte[] imageData = downloadImageFromUrl(normalizedUrl);

			String fileName = generateFileNameFromUrl(normalizedUrl, index);
			String key = buildS3Key(dirName, fileName);

			log.debug("[S3] 이미지 업로드 진행 - URL: {}, 키: {}", normalizedUrl, key);

			// byte[] 데이터를 InputStream으로 변환
			try (InputStream inputStream = new ByteArrayInputStream(imageData)) {
				S3Resource s3Resource = s3Template.upload(awsProperties.s3().bucket(), key, inputStream);
				String uploadedUrl = s3Resource.getURL().toString();

				log.debug("[S3] 이미지 업로드 완료 - 원본 URL: {}, S3 URL: {}", normalizedUrl, uploadedUrl);
				return uploadedUrl;
			}

		} catch (Exception e) {
			log.error("[S3] 단일 이미지 업로드 실패 - URL: {}, 에러: {}", imageUrl, e.getMessage(), e);
			throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
		}
	}

	/**
	 * URL에서 이미지 데이터 다운로드
	 */
	private byte[] downloadImageFromUrl(String imageUrl) throws IOException {
		URL url = URI.create(imageUrl).toURL();
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

		try (InputStream inputStream = connection.getInputStream();
			 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[4096];
			int bytesRead;
			int totalBytes = 0;

			while ((bytesRead = inputStream.read(buffer)) != -1) {
				totalBytes += bytesRead;
				if (totalBytes > MAX_FILE_SIZE) {
					throw new AwsException(GlobalErrorCode.S3_FILE_SIZE_LIMIT);
				}
				outputStream.write(buffer, 0, bytesRead);
			}

			return outputStream.toByteArray();
		}
	}

	/**
	 * 파일 검증
	 */
	private void validateFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
		}

		if (file.getSize() > MAX_FILE_SIZE) {
			throw new AwsException(GlobalErrorCode.S3_FILE_SIZE_LIMIT);
		}

		String contentType = file.getContentType();
		if (contentType == null || !contentType.startsWith("image/")) {
			throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
		}

		String filename = file.getOriginalFilename();
		if (filename != null) {
			String extension = StringUtils.getFilenameExtension(filename.toLowerCase());
			if (!Set.of("jpg", "jpeg", "png", "gif", "webp").contains(extension)) {
				throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
			}
		}
	}

	/**
	 * 파일명 생성
	 */
	private String generateFileName(String originalFilename, String dirName) {
		String extension = StringUtils.getFilenameExtension(originalFilename);
		String uuid = UUID.randomUUID().toString();
		return dirName + "_" + uuid + "." + extension;
	}

	/**
	 * URL에서 파일명 생성
	 */
	private String generateFileNameFromUrl(String imageUrl, int index) {
		String extension = extractExtensionFromUrl(imageUrl);
		String uuid = UUID.randomUUID().toString();
		return String.format("image_%d_%s.%s", index, uuid, extension);
	}

	/**
	 * S3 키 생성
	 */
	private String buildS3Key(String dirName, String fileName) {
		String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
		return String.format("%s/%s/%s", dirName, dateDir, fileName);
	}

	/**
	 * URL 정규화
	 */
	private String normalizeUrl(String url) {
		if (url == null || url.trim().isEmpty()) {
			throw new AwsException(GlobalErrorCode.S3_URL_NOT_VALID);
		}

		url = url.trim();
		if (!url.startsWith("http://") && !url.startsWith("https://")) {
			url = "https://" + url;
		}

		return url;
	}

	/**
	 * URL에서 확장자 추출
	 */
	private String extractExtensionFromUrl(String url) {
		try {
			// Java 20에서 deprecated된 URL 생성자 대신 URI 사용
			URI uri = URI.create(url);
			String path = uri.getPath();
			String extension = StringUtils.getFilenameExtension(path);
			return extension != null ? extension : "jpg";
		} catch (Exception e) {
			return "jpg";
		}
	}

	/**
	 * S3 URL에서 키 추출
	 */
	private String extractKeyFromUrl(String s3Url) {
		if (s3Url.contains("amazonaws.com/")) {
			return s3Url.substring(s3Url.indexOf("amazonaws.com/") + 14);
		}
		throw new AwsException(GlobalErrorCode.S3_URL_NOT_VALID);

	}

	/**
	 * 단일 파일을 S3에 업로드
	 */
	private String uploadSingleFile(MultipartFile file, String dirName) {
		try {
			validateFile(file);

			String fileName = generateFileName(file.getOriginalFilename(), dirName);
			String key = buildS3Key(dirName, fileName);

			log.debug("[S3] 파일 업로드 진행 - 파일명: {}, 키: {}", fileName, key);

			S3Resource s3Resource = s3Template.upload(awsProperties.s3().bucket(), key, file.getInputStream());
			String uploadedUrl = s3Resource.getURL().toString();

			log.debug("[S3] 파일 업로드 완료 - 파일명: {}, S3 URL: {}", fileName, uploadedUrl);
			return uploadedUrl;

		} catch (IOException e) {
			log.error("[S3] 단일 파일 업로드 실패 - 파일명: {}, 에러: {}", file.getOriginalFilename(), e.getMessage(), e);
			throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
		}
	}

	public String updateImage(String url, MultipartFile multipartFile) {
		try {
			// 기존 이미지 삭제
			if (StringUtils.hasText(url)) {
				deleteFile(url);
				log.info("[S3] 기존 이미지 삭제 완료 - URL: {}", url);
			}

			// 새 이미지 업로드
			String fileName = generateFileName(multipartFile.getOriginalFilename(), "updated");
			String key = buildS3Key("updated", fileName);

			validateFile(multipartFile);

			S3Resource s3Resource = s3Template.upload(awsProperties.s3().bucket(), key, multipartFile.getInputStream());
			String uploadedUrl = s3Resource.getURL().toString();

			log.info("[S3] 이미지 업데이트 완료 - 새 URL: {}", uploadedUrl);
			return uploadedUrl;

		} catch (IOException e) {
			log.error("[S3] 이미지 업데이트 실패 - 기존 URL: {}, 에러: {}", url, e.getMessage(), e);
			throw new AwsException(GlobalErrorCode.S3_UPLOAD_FAILED);
		}
	}
}
