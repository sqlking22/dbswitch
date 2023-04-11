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

import com.gitee.dbswitch.admin.type.SupportDbTypeEnum;
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
@Table(name = "DBSWITCH_DATABASE_CONNECTION")
public class DatabaseConnectionEntity {

  @Id
  @KeySql(useGeneratedKeys = true)
  @Column(name = "id", insertable = false, updatable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "type")
  private SupportDbTypeEnum type;

  @Column(name = "version")
  private String version;

  @Column(name = "driver")
  private String driver;

  @Column(name = "url")
  private String url;

  @Column(name = "username")
  private String username;

  @Column(name = "password")
  private String password;

  @Column(name = "create_time", insertable = false, updatable = false)
  private Timestamp createTime;

  @Column(name = "update_time", insertable = false, updatable = false)
  private Timestamp updateTime;
}
