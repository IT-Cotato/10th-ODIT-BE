package com.odit.backend.infra.async.entity;

import java.time.LocalDateTime;

import org.springframework.data.redis.core.RedisHash;

import com.odit.backend.infra.async.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "summaryTask", timeToLive = 3600)
public class SummaryTask {

	private String id;

	private String url;

	@Builder.Default
	private TaskStatus status = TaskStatus.PENDING;

	@Builder.Default
	private Integer progress = 0;

	private Object result;
	private LocalDateTime startedAt;
	private LocalDateTime completedAt;
	private String currentStep;

	public void startCrawling() {
		this.status = TaskStatus.CRAWLING;
		this.startedAt = LocalDateTime.now();
		this.currentStep = status.getMessage();
		this.progress = 10;
	}

	public void startAnalyzing() {
		this.status = TaskStatus.ANALYZING;
		this.currentStep = status.getMessage();
		this.progress = 50;
	}

	public void completeTask(Object taskResult) {
		this.status = TaskStatus.COMPLETED;
		this.result = taskResult;
		this.progress = 100;
		this.completedAt = LocalDateTime.now();
		this.currentStep = status.getMessage();
	}

	public void failTask() {
		this.status = TaskStatus.FAILED;
		this.completedAt = LocalDateTime.now();
		this.currentStep = status.getMessage();
	}

	public void cancelTask() {
		this.status = TaskStatus.CANCELLED;
		this.completedAt = LocalDateTime.now();
		this.currentStep = status.getMessage();
	}

	public boolean isCompleted() {
		return status == TaskStatus.COMPLETED || status == TaskStatus.FAILED || status == TaskStatus.CANCELLED;
	}
}
