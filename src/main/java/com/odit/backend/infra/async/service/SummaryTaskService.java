package com.odit.backend.infra.async.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.odit.backend.domain.ai.dto.response.ContentListResponse;
import com.odit.backend.global.error.GlobalErrorCode;
import com.odit.backend.infra.async.entity.SummaryTask;
import com.odit.backend.infra.async.enums.TaskStatus;
import com.odit.backend.infra.async.exception.AsyncException;
import com.odit.backend.infra.async.repository.SummaryTaskRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SummaryTaskService {

	private final SummaryTaskRepository summaryTaskRepository;

	/**
	 * 새로운 요약 작업 생성
	 *
	 * @param url 처리할 URL
	 * @return 생성된 작업 객체
	 */
	public SummaryTask createTask(String url) {
		String taskId = generateTaskId();
		SummaryTask task = SummaryTask.builder()
			.id(taskId)
			.url(url)
			.status(TaskStatus.PENDING)
			.build();

		log.info("새로운 작업 생성됨: taskId={}, url={}", taskId, url);
		return summaryTaskRepository.save(task);
	}

	/**
	 * 작업 ID로 작업 조회
	 *
	 * @param taskId 작업 ID
	 * @return 작업 객체
	 * @throws AsyncException 작업을 찾을 수 없는 경우
	 */
	public SummaryTask findTask(String taskId) {
		return summaryTaskRepository.findById(taskId)
			.orElseThrow(() -> new AsyncException(GlobalErrorCode.TASK_NOT_FOUND));
	}

	/**
	 * 작업 취소
	 *
	 * @param taskId 작업 ID
	 * @throws AsyncException 이미 완료된 작업인 경우
	 */
	public void cancelTask(String taskId) {
		SummaryTask task = findTask(taskId);
		if (task.isCompleted()) {
			throw new AsyncException(GlobalErrorCode.TASK_ALREADY_COMPLETE);
		}
		task.cancelTask();
		summaryTaskRepository.save(task);
		log.info("작업 취소: taskId={}, status={}, progress={}%", taskId, task.getStatus(), task.getProgress());
	}

	public void updateTask(String taskId, TaskStatus status, ContentListResponse response) {
		SummaryTask task = findTask(taskId);
		switch (status) {
			case CRAWLING -> task.startCrawling();
			case ANALYZING -> task.startAnalyzing();
			case COMPLETED -> task.completeTask(response);
			case CANCELLED -> task.cancelTask();
			case FAILED -> task.failTask();
		}
		summaryTaskRepository.save(task);
	}

	private String generateTaskId() {
		return UUID.randomUUID().toString();
	}

}