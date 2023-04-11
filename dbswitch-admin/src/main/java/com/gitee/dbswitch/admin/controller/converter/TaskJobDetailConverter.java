// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.controller.converter;

import com.gitee.dbswitch.admin.common.converter.AbstractConverter;
import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.model.response.TaskJobDetailResponse;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import com.gitee.dbswitch.admin.type.ScheduleModeEnum;
import java.util.Objects;

public class TaskJobDetailConverter extends
    AbstractConverter<AssignmentJobEntity, TaskJobDetailResponse> {

  @Override
  public TaskJobDetailResponse convert(AssignmentJobEntity assignmentJobEntity) {
    if (Objects.isNull(assignmentJobEntity)) {
      return null;
    }

    //ScheduleService scheduleService = SpringUtil.getBean(ScheduleService.class);
    TaskJobDetailResponse response = new TaskJobDetailResponse();
    response.setJobId(assignmentJobEntity.getId());
    response.setAssignmentId(assignmentJobEntity.getAssignmentId());
    response.setStartTime(assignmentJobEntity.getStartTime());
    response.setFinishTime(assignmentJobEntity.getFinishTime());
    if (assignmentJobEntity.getStatus() == JobStatusEnum.RUNNING.getValue()) {
      response.setDuration(
          (System.currentTimeMillis() - assignmentJobEntity.getStartTime().getTime()) / 1000);
    } else {
      response.setDuration(
          (assignmentJobEntity.getFinishTime().getTime() - assignmentJobEntity.getStartTime()
              .getTime())
              / 1000);
    }
    response.setStatus(assignmentJobEntity.getStatus());
    response.setJobStatus(JobStatusEnum.of(assignmentJobEntity.getStatus()).getName());
    response.setScheduleMode(ScheduleModeEnum.of(assignmentJobEntity.getScheduleMode()).getName());
    response.setErrorLog(assignmentJobEntity.getErrorLog());

    return response;
  }

}
