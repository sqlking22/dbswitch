// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbcommon.database;

import com.gitee.dbswitch.dbcommon.domain.StatementResultSet;
import java.util.List;
import javax.sql.DataSource;

/**
 * 数据库操作器接口定义
 *
 * @author tang
 */
public interface IDatabaseOperator {

  /**
   * 获取数据源
   *
   * @return 数据源
   */
  DataSource getDataSource();

  /**
   * 获取读取(fetch)数据的批次大小
   *
   * @return 批次大小
   */
  int getFetchSize();

  /**
   * 设置读取(fetch)数据的批次大小
   *
   * @param size 批次大小
   */
  void setFetchSize(int size);

  /**
   * 生成查询指定字段的select查询SQL语句
   *
   * @param schemaName 模式名称
   * @param tableName  表名称
   * @param fields     字段列表
   * @return 查询指定字段的select查询SQL语句
   */
  String getSelectTableSql(String schemaName, String tableName, List<String> fields);

  /**
   * 获取指定schema下表的按主键有序的结果集
   *
   * @param schemaName 模式名称
   * @param tableName  表名称
   * @param fields     字段列表
   * @param orders     排序字段列表
   * @return 结果集包装对象
   */
  StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields,
      List<String> orders);

  /**
   * 获取指定schema下表的结果集
   *
   * @param schemaName 模式名称
   * @param tableName  表名称
   * @param fields     字段列表
   * @return 结果集包装对象
   */
  StatementResultSet queryTableData(String schemaName, String tableName, List<String> fields);

  /**
   * 清除指定表的所有数据
   *
   * @param schemaName 模式名称
   * @param tableName  表名称
   */
  void truncateTableData(String schemaName, String tableName);

  /**
   * 删除指定物理表
   *
   * @param schemaName 模式名称
   * @param tableName  表名称
   */
  void dropTable(String schemaName, String tableName);
}
