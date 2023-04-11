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

import com.gitee.dbswitch.admin.type.ScheduleModeEnum;
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
@Table(name = "DBSWITCH_ASSIGNMENT_TASK")
public class AssignmentTaskEntity {

  @Id
  @KeySql(useGeneratedKeys = true)
  @Column(name = "id", insertable = false, updatable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "schedule_mode")
  private ScheduleModeEnum scheduleMode;

  @Column(name = "cron_expression")
  private String cronExpression;

  @Column(name = "published")
  private Boolean published;

  @Column(name = "content")
  private String content;

  @Column(name = "job_key")
  private String jobKey;

  @Column(name = "create_time", insertable = false, updatable = false)
  private Timestamp createTime;

  @Column(name = "update_time", insertable = false, updatable = false)
  private Timestamp updateTime;
}
