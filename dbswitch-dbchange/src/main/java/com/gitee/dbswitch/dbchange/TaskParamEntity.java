// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbchange;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

/**
 * 任务参数实体类定义
 *
 * @author tang
 */
@Data
@Builder
@AllArgsConstructor
public class TaskParamEntity {

  /**
   * 老表的数据源
   */
  @NonNull
  private DataSource oldDataSource;

  /**
   * 老表的schema名
   */
  @NonNull
  private String oldSchemaName;

  /**
   * 老表的table名
   */
  @NonNull
  private String oldTableName;

  /**
   * 新表的数据源
   */
  @NonNull
  private DataSource newDataSource;

  /**
   * 新表的schema名
   */
  @NonNull
  private String newSchemaName;

  /**
   * 新表的table名
   */
  @NonNull
  private String newTableName;

  /**
   * 字段列
   */
  private List<String> fieldColumns;

  /**
   * 字段名映射关系
   */
  @NonNull
  @Builder.Default
  private Map<String, String> columnsMap = Collections.EMPTY_MAP;
}
