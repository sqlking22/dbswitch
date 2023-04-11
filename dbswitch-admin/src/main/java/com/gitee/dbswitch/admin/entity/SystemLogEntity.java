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
@Table(name = "DBSWITCH_SYSTEM_LOG")
public class SystemLogEntity {

  @Id
  @KeySql(useGeneratedKeys = true)
  @Column(name = "id", insertable = false, updatable = false)
  private Long id;

  @Column(name = "type")
  private Integer type;

  @Column(name = "username")
  private String username;

  @Column(name = "ip_address")
  private String ipAddress;

  @Column(name = "module_name")
  private String moduleName;

  @Column(name = "content")
  private String content;

  @Column(name = "url_path")
  private String urlPath;

  @Column(name = "user_agent")
  private String userAgent;

  @Column(name = "failed")
  private Boolean failed;

  @Column(name = "exception")
  private String exception;

  @Column(name = "elapse_seconds")
  private Long elapseSeconds;

  @Column(name = "create_time", insertable = false, updatable = false)
  private Timestamp createTime;
}
