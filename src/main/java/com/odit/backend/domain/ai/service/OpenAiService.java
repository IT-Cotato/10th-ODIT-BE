package com.odit.backend.domain.ai.service;

import static com.odit.backend.global.error.GlobalErrorCode.*;
import static com.odit.backend.infra.async.enums.TaskStatus.*;

import java.util.concurrent.CompletableFuture;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.odit.backend.domain.ai.dto.response.ContentListResponse;
import com.odit.backend.domain.ai.dto.response.CrawlCompletionResponse;
import com.odit.backend.domain.ai.exception.AiException;
import com.odit.backend.infra.async.service.SummaryTaskService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AI 요약 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OpenAiService {

	private final ChatClient chatClient;
	private final ContentService contentService;
	private final SummaryTaskService summaryTaskService;

	@Value("classpath:/prompts/culture-info-prompt.st")
	private Resource prompt;
	@Value("classpath:/prompts/culture-info-system.st")
	private Resource system;

	/**
	 * 비동기 AI 요약 작업 시작 (Polling을 위한 새로운 메서드)
	 *
	 * @param taskId 작업 ID
	 * @param url    처리할 URL
	 */
	@Async("aiSummaryTaskExecutor")
	public void startSummaryAsync(String taskId, String url) {
		log.info("[AI] 비동기 요약 작업 시작 - TaskId: {}, URL: {}", taskId, url);

		summaryTaskService.updateTask(taskId, CRAWLING, null);
		contentService.extractContents(url)
			.thenCompose(extractedContent -> {
				log.debug("[AI] 웹페이지 크롤링 완료 - TaskId: {}", taskId);
				summaryTaskService.updateTask(taskId, ANALYZING, null);
				return processWithAI(extractedContent);
			})
			.thenAccept(result -> {
				log.info("[AI] 요약 작업 완료 - TaskId: {}", taskId);
				summaryTaskService.updateTask(taskId, COMPLETED, result);
			})
			.exceptionally(throwable -> {
				log.error("[AI] 요약 작업 실패 - TaskId: {}, Error: {}", taskId, throwable.getMessage(), throwable);
				summaryTaskService.updateTask(taskId, FAILED, null);
				return null;
			});
	}

	private CompletableFuture<ContentListResponse> processWithAI(CrawlCompletionResponse extractedContent) {
		return CompletableFuture.supplyAsync(() -> {
			log.debug("[AI] AI 처리 시작");
			return performAiAnalysis(extractedContent);
		});
	}

	/**
	 * 실제 AI 분석 수행
	 *
	 * @param extractedContent 크롤링된 컨텐츠
	 * @return AI 분석 결과
	 */
	private ContentListResponse performAiAnalysis(CrawlCompletionResponse extractedContent) {
		BeanOutputConverter<ContentListResponse> converter = new BeanOutputConverter<>(ContentListResponse.class);
		PromptTemplate promptTemplate = generatePromptTemplate(extractedContent);

		String aiResponse = callAiService(promptTemplate, converter);
		ContentListResponse convertedResponse = convertAiResponse(aiResponse, converter);

		return buildContentListResponse(convertedResponse, extractedContent);
	}

	private String callAiService(PromptTemplate promptTemplate, BeanOutputConverter<ContentListResponse> converter) {
		String response = chatClient.prompt()
			.system(system)
			.user(promptTemplate.render() + converter.getFormat())
			.call()
			.content();

		if (response == null || response.trim().isEmpty()) {
			throw new AiException(AI_PROCESSING_FAILED);
		}

		return response;
	}

	private ContentListResponse convertAiResponse(String aiResponse,
		BeanOutputConverter<ContentListResponse> converter) {
		try {
			return converter.convert(aiResponse);
		} catch (Exception e) {
			log.error("[AI] 응답 변환 실패: {}", e.getMessage());
			throw new AiException(AI_CONVERSION_FAILED);
		}
	}

	private ContentListResponse buildContentListResponse(ContentListResponse convertedResponse,
		CrawlCompletionResponse extractedContent) {
		return ContentListResponse.builder()
			.contentResponseList(convertedResponse.contentResponseList())
			.imageSrcList(extractedContent.imageSrcList())
			.build();
	}

	/**
	 * 프롬프트 템플릿 생성
	 *
	 * @param extractedContent 크롤링된 컨텐츠
	 * @return 생성된 프롬프트 템플릿
	 */
	private PromptTemplate generatePromptTemplate(final CrawlCompletionResponse extractedContent) {
		try {
			PromptTemplate promptTemplate = new PromptTemplate(prompt);
			promptTemplate.add("extractedContent", extractedContent.crawlingData());
			return promptTemplate;
		} catch (Exception e) {
			log.error("[AI] 프롬프트 생성 실패: {}", e.getMessage());
			throw new AiException(AI_PROMPT_GENERATE_FAILED);
		}
	}
}
