// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.data.entity;

import com.gitee.dbswitch.common.entity.PatternMapper;
import com.gitee.dbswitch.common.type.DBTableType;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.Data;

@Data
public class SourceDataSourceProperties {

  private String url;
  private String driverClassName;
  private String username;
  private String password;
  private String driverPath;
  private Long connectionTimeout = TimeUnit.SECONDS.toMillis(60);
  private Long maxLifeTime = TimeUnit.MINUTES.toMillis(60);

  private Integer fetchSize = 5000;
  private String sourceSchema = "";
  private String tableType = "TABLE";
  private String sourceIncludes = "";
  private String sourceExcludes = "";
  private List<PatternMapper> regexTableMapper;
  private List<PatternMapper> regexColumnMapper;
}
