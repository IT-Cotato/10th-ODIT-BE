package com.adit.backend.global.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.support.RetryTemplate;

import com.adit.backend.domain.ai.util.LoggingAdvisor;
import com.adit.backend.global.config.property.OpenAiProperties;

import io.micrometer.observation.ObservationRegistry;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AiConfig {

	private final ToolCallingManager toolCallingManager;
	private final LoggingAdvisor loggingAdvisor;
	private final OpenAiProperties openAiProperties;
	private final RetryTemplate retryTemplate;

	@Bean
	ChatMemory chatMemory() {
		return MessageWindowChatMemory.builder().build();
	}

	@Bean
	public ChatClient chatClient() {
		OpenAiApi openAiApi = OpenAiApi.builder()
			.apiKey(openAiProperties.apiKey())
			.build();

		OpenAiChatOptions chatOptions = OpenAiChatOptions.builder()
			.model(openAiProperties.chat().options().model())
			.maxTokens(openAiProperties.chat().options().maxTokens())
			.build();

		ChatModel chatModel = OpenAiChatModel.builder()
			.openAiApi(openAiApi)
			.defaultOptions(chatOptions)
			.toolCallingManager(toolCallingManager)
			.observationRegistry(ObservationRegistry.create())
			.retryTemplate(retryTemplate)
			.build();

		return ChatClient
			.builder(chatModel)
			.defaultAdvisors(loggingAdvisor)
			.build();
	}
}