package com.odit.backend.infra.async.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.odit.backend.infra.async.entity.SummaryTask;

@Repository
public interface SummaryTaskRepository extends CrudRepository<SummaryTask, String> {
}
