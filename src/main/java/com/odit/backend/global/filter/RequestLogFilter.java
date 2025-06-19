package com.odit.backend.global.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

/**
 * HTTP 요청/응답을 로그에 남기는 Filter.
 * 적용 프로파일: {@code blue, green}
 */

@Slf4j
@Component
@Profile("blue, green")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestLogFilter extends GenericFilterBean {

	public static final String LOG_DELIMITER = "=";
	private static final String REQUEST_LOG_SEPARATOR = "\n";
	private static final String REQUEST_LOG_INDENT = "  ";
	private static final String REQUEST_LOG_PREFIX = "REQUEST: ";
	private static final String RESPONSE_LOG_PREFIX = "RESPONSE: ";

	private static String joinMapIntoString(Map<String, String> logMap) {
		return logMap.entrySet().stream()
			.filter(e -> StringUtils.isNotBlank(e.getValue()))
			.map(e -> String.join(" ", e.getKey(), e.getValue()))
			.collect(Collectors.joining(REQUEST_LOG_SEPARATOR));
	}

	@SneakyThrows
	private void logRequest(ContentCachingRequestWrapper request) {
		String parameters = parametersToString(request.getParameterMap());
		String headers = headersToString(Collections.list(request.getHeaderNames()), request::getHeader);
		String body = new String(request.getContentAsByteArray());
		Map<String, String> logMap = new LinkedHashMap<>();
		logMap.put(REQUEST_LOG_INDENT + "Parameters:", parameters);
		logMap.put(REQUEST_LOG_INDENT + "Headers:", headers);
		logMap.put(REQUEST_LOG_INDENT + "Body:", body);
		String logString = joinMapIntoString(logMap);
		log.info(REQUEST_LOG_PREFIX + request.getMethod() + request.getRequestURI() + logString);
	}

	@SneakyThrows
	private void logResponse(ContentCachingResponseWrapper response) throws IOException {
		String headers = headersToString(response.getHeaderNames(), response::getHeader);
		String body = new String(response.getContentAsByteArray());
		Map<String, String> logMap = new LinkedHashMap<>();
		logMap.put(REQUEST_LOG_INDENT + "Status", String.valueOf(response.getStatus()));
		logMap.put(REQUEST_LOG_INDENT + "Headers:", headers);
		logMap.put(REQUEST_LOG_INDENT + "Body:", body);
		String logString = joinMapIntoString(logMap);
		log.info(RESPONSE_LOG_PREFIX + logString);
		response.copyBodyToResponse();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest)request);
		ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(
			(HttpServletResponse)response);
		chain.doFilter(requestWrapper, responseWrapper);
		logRequest(requestWrapper);
		logResponse(responseWrapper);
	}

	@SneakyThrows
	private String headersToString(Collection<String> headerNames, UnaryOperator<String> headerValueResolver) {
		return headerNames.stream()
			.map(header -> String.join(LOG_DELIMITER, header, headerValueResolver.apply(header)))
			.collect(Collectors.joining(REQUEST_LOG_SEPARATOR));
	}

	private String parametersToString(Map<String, String[]> parameterMap) {
		return parameterMap.entrySet().stream()
			.map(param -> String.join(LOG_DELIMITER, param.getKey(), Arrays.toString(param.getValue())))
			.collect(Collectors.joining(REQUEST_LOG_SEPARATOR));
	}
}