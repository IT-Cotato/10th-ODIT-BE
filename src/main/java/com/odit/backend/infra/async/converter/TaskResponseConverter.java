package com.odit.backend.infra.async.converter;

import org.springframework.stereotype.Component;

import com.odit.backend.infra.async.entity.SummaryTask;
import com.odit.backend.infra.async.response.TaskStartResponse;
import com.odit.backend.infra.async.response.TaskStatusResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class TaskResponseConverter {

	public TaskStartResponse toTaskStartResponse(SummaryTask task) {
		if (task == null) {
			log.warn("TaskStartResponse 변환 시 null task가 전달됨");
			throw new IllegalArgumentException("Task는 null일 수 없습니다");
		}

		log.debug("Task 시작 응답 변환 - TaskId: {}, Status: {}", task.getId(), task.getStatus());

		return TaskStartResponse.builder()
			.taskId(task.getId())
			.status(task.getStatus())
			.message(task.getStatus().getMessage())
			.startedAt(task.getStartedAt())
			.build();
	}

	public TaskStatusResponse toTaskStatusResponse(SummaryTask task) {
		if (task == null) {
			log.warn("TaskStatusResponse 변환 시 null task가 전달됨");
			throw new IllegalArgumentException("Task는 null일 수 없습니다");
		}

		log.debug("Task 상태 변환 - TaskId: {}, Status: {}", task.getId(), task.getStatus());

		return TaskStatusResponse.builder()
			.taskId(task.getId())
			.status(task.getStatus())
			.statusMessage(task.getStatus().getMessage())
			.progress(task.getProgress())
			.result(task.getResult())
			.createdAt(task.getStartedAt())
			.completedAt(task.getCompletedAt())
			.build();
	}
}
