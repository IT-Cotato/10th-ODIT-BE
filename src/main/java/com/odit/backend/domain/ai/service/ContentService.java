package com.odit.backend.domain.ai.service;

import java.util.concurrent.CompletableFuture;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.odit.backend.domain.ai.dto.response.CrawlCompletionResponse;
import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.infra.crawler.exception.CrawlingException;
import com.odit.backend.infra.crawler.service.WebContentCrawlingService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentService {
	private final WebContentCrawlingService crawlingService;

	public CompletableFuture<CrawlCompletionResponse> extractContents(String url) {
		validateUrl(url);
		return crawlingService.crawlAsync(url);
	}

	private void validateUrl(String url) {
		if (!StringUtils.hasText(url) || !url.startsWith("http")) {
			log.error("[검증되지 않은 URL] : {}", url);
			throw new CrawlingException(GlobalErrorCode.INVALID_URL);
		}
	}
}