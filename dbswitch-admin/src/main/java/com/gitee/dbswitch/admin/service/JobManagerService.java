// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.service;

import com.gitee.dbswitch.admin.common.converter.ConverterFactory;
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.controller.converter.TaskJobDetailConverter;
import com.gitee.dbswitch.admin.dao.AssignmentJobDAO;
import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.model.response.TaskJobDetailResponse;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import com.gitee.dbswitch.admin.util.PageUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JobManagerService {

  @Resource
  private AssignmentJobDAO assignmentJobDAO;
  @Resource
  private ScheduleService scheduleService;

  @EventListener(ApplicationReadyEvent.class)
  public void initAfterRestart() {
    String errorLog = "Job was canceled by restart dbswitch program! ";
    try {
      assignmentJobDAO.updateStatus(JobStatusEnum.RUNNING, JobStatusEnum.FAIL, errorLog);
      log.info("Success to revise job status");
    } catch (Throwable t) {
      log.error("Error when revise job status from running to failed:", t);
    }
  }

  public PageResult<TaskJobDetailResponse> listJobs(Long assignmentId, Integer page, Integer size) {
    Supplier<List<TaskJobDetailResponse>> method = () -> {
      List<AssignmentJobEntity> jobs = assignmentJobDAO.getByAssignmentId(assignmentId);
      return ConverterFactory.getConverter(TaskJobDetailConverter.class).convert(jobs);
    };

    return PageUtils.getPage(method, page, size);
  }

  public Result<TaskJobDetailResponse> detailJob(Long jobId) {
    AssignmentJobEntity job = assignmentJobDAO.getById(jobId);
    if (Objects.isNull(job)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "jobId=" + jobId.toString());
    }

    return Result.success(ConverterFactory.getConverter(TaskJobDetailConverter.class).convert(job));
  }

  public Result<Boolean> cancelJob(Long jobId) {
    AssignmentJobEntity job = assignmentJobDAO.getById(jobId);
    if (Objects.isNull(job)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "jobId=" + jobId.toString());
    }

    if (job.getStatus() == JobStatusEnum.RUNNING.getValue()) {
      scheduleService.cancelJob(jobId);
    }

    return Result.success(true);
  }

}
