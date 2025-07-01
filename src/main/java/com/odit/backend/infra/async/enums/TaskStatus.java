package com.odit.backend.infra.async.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TaskStatus {
	PENDING("대기 중"),
	CRAWLING("웹페이지 크롤링 중"),
	ANALYZING("AI 분석 중"),
	COMPLETED("완료"),
	FAILED("실패"),
	CANCELLED("취소됨");

	private final String message;

	public boolean isTerminal() {
		return this == COMPLETED || this == FAILED || this == CANCELLED;
	}
}
