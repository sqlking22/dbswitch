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
import lombok.experimental.SuperBuilder;
import tk.mybatis.mapper.annotation.KeySql;

@SuperBuilder
@NoArgsConstructor
@Data
@Entity
@Table(name = "DBSWITCH_JOB_LOGBACK")
public class JobLogbackEntity {

  @Id
  @KeySql(useGeneratedKeys = true)
  @Column(name = "id", insertable = false, updatable = false)
  private Long id;

  @Column(name = "uuid")
  private String uuid;

  @Column(name = "content")
  private String content;

  @Column(name = "create_time", insertable = false, updatable = false)
  private Timestamp createTime;
}
