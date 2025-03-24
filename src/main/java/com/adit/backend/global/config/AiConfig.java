package com.adit.backend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import com.adit.backend.domain.ai.util.LoggingAdvisor;

import io.micrometer.observation.ObservationRegistry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AiConfig {

	@Value("${spring.ai.openai.api-key}")
	private String apiKey;

	@Value("${spring.ai.openai.chat.options.model}")
	private String defaultModel;

	@Value("${spring.ai.openai.chat.options.max-tokens}")
	private int maxCompletionToken;

	private final ToolCallingManager toolCallingManager;
	private final RetryTemplate retryTemplate;

	@Bean
	ChatMemory chatMemory() {
		return new InMemoryChatMemory();
	}

	/**
	 * Chat Client dependency injection.
	 *
	 * @return the chat client
	 */
	@Bean
	public ChatClient chatClient() {
		// OpenAiApi 인스턴스 생성
		OpenAiApi openAiApi = OpenAiApi.builder()
			.apiKey(apiKey)
			.build();

		// OpenAiAPi 모델 기본 설정 (모델, 최대 토큰수)
		OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
			.model(defaultModel)
			.maxCompletionTokens(maxCompletionToken)
			.build();

		// ChatModel 생성 (1.0.0-M6)
		ChatModel chatModel = OpenAiChatModel.builder()
			.openAiApi(openAiApi)
			.defaultOptions(chatOptions)
			.toolCallingManager(toolCallingManager)
			.observationRegistry(ObservationRegistry.create())
			.retryTemplate(retryTemplate)
			.build();

		return ChatClient
			.builder(chatModel).defaultAdvisors(
				new MessageChatMemoryAdvisor(chatMemory(),
					AbstractChatMemoryAdvisor.DEFAULT_CHAT_MEMORY_CONVERSATION_ID, 10),
				new LoggingAdvisor()
			)
			.build();
	}
}