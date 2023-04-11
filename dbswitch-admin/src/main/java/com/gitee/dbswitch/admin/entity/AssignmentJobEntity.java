// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.entity;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tk.mybatis.mapper.annotation.KeySql;

@NoArgsConstructor
@Data
@Entity
@Table(name = "DBSWITCH_ASSIGNMENT_JOB")
public class AssignmentJobEntity {

  @Id
  @KeySql(useGeneratedKeys = true)
  @Column(name = "id", insertable = false, updatable = false)
  private Long id;

  @Column(name = "assignment_id")
  private Long assignmentId;

  @Column(name = "job_key")
  private String jobKey;

  @Column(name = "schedule_mode")
  private Integer scheduleMode;

  @Column(name = "start_time")
  private Timestamp startTime;

  @Column(name = "finish_time")
  private Timestamp finishTime;

  @Column(name = "status")
  private Integer status;

  @Column(name = "error_log")
  private String errorLog;

  @Column(name = "create_time", insertable = false, updatable = false)
  private Timestamp createTime;

  @Column(name = "update_time", insertable = false, updatable = false)
  private Timestamp updateTime;
}
