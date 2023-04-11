// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.dao;

import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.mapper.AssignmentJobMapper;
import com.gitee.dbswitch.admin.model.ops.OpsTaskJobTrend;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

@Repository
public class AssignmentJobDAO {

  @Resource
  private AssignmentJobMapper assignmentJobMapper;

  public AssignmentJobEntity newAssignmentJob(Long assignmentId, Integer scheduleMode,
      String jobKey) {
    AssignmentJobEntity assignmentJobEntity = new AssignmentJobEntity();
    assignmentJobEntity.setAssignmentId(assignmentId);
    assignmentJobEntity.setJobKey(jobKey);
    assignmentJobEntity.setScheduleMode(scheduleMode);
    assignmentJobEntity.setStartTime(new Timestamp(System.currentTimeMillis()));
    assignmentJobEntity.setFinishTime(new Timestamp(System.currentTimeMillis()));
    assignmentJobEntity.setStatus(JobStatusEnum.RUNNING.getValue());
    assignmentJobMapper.insertSelective(assignmentJobEntity);
    return assignmentJobEntity;
  }

  public AssignmentJobEntity getById(Long id) {
    return assignmentJobMapper.selectByPrimaryKey(id);
  }

  public List<AssignmentJobEntity> getByAssignmentId(Long assignmentId) {
    Objects.requireNonNull(assignmentId, "assignmentId不能为null");

    AssignmentJobEntity condition = new AssignmentJobEntity();
    condition.setAssignmentId(assignmentId);

    Example example = new Example(AssignmentJobEntity.class);
    example.createCriteria().andEqualTo(condition);
    example.orderBy("createTime").desc();
    return assignmentJobMapper.selectByExample(example);
  }

  public void updateSelective(AssignmentJobEntity assignmentJobEntity) {
    Objects.requireNonNull(assignmentJobEntity.getId(), "AssignmentJob的id不能为null");
    assignmentJobMapper.updateByPrimaryKeySelective(assignmentJobEntity);
  }

  public int getCountByStatus(JobStatusEnum status) {
    AssignmentJobEntity condition = new AssignmentJobEntity();
    condition.setStatus(status.getValue());

    Example example = new Example(AssignmentJobEntity.class);
    example.createCriteria().andEqualTo(condition);
    return assignmentJobMapper.selectCountByExample(example);
  }

  public int getTotalCount() {
    return assignmentJobMapper.selectCountByExample(null);
  }

  public List<OpsTaskJobTrend> queryTaskJobTrend(Integer days) {
    return assignmentJobMapper.queryTaskJobTrend(days);
  }

  public void updateStatus(JobStatusEnum originalStatus, JobStatusEnum targetStatus, String errorLog) {
    AssignmentJobEntity updateSet = new AssignmentJobEntity();
    updateSet.setStatus(targetStatus.getValue());
    if (JobStatusEnum.FAIL.equals(targetStatus)) {
      updateSet.setErrorLog(errorLog);
    }
    Example condition = Example.builder(AssignmentJobEntity.class)
        .where(Sqls.custom().andEqualTo("status", originalStatus.getValue()))
        .build();
    assignmentJobMapper.updateByExampleSelective(updateSet, condition);
  }

}
