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

import com.gitee.dbswitch.dbcommon.constant.Constants;
import com.gitee.dbswitch.dbcommon.domain.StatementResultSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据读取抽象基类
 *
 * @author tang
 */
@Slf4j
public abstract class AbstractDatabaseOperator implements IDatabaseOperator {

  protected DataSource dataSource;

  protected int fetchSize;

  public AbstractDatabaseOperator(DataSource dataSource) {
    this.dataSource = Objects.requireNonNull(dataSource, "数据源非法,为null");
    this.fetchSize = Constants.DEFAULT_FETCH_SIZE;
  }

  @Override
  public DataSource getDataSource() {
    return this.dataSource;
  }

  @Override
  public int getFetchSize() {
    return this.fetchSize;
  }

  @Override
  public void setFetchSize(int size) {
    if (size < Constants.MINIMUM_FETCH_SIZE) {
      throw new IllegalArgumentException(
          "设置的批量处理行数的大小fetchSize不得小于" + Constants.MINIMUM_FETCH_SIZE);
    }

    this.fetchSize = size;
  }

  /**
   * 已经指定的查询SQL语句查询数据结果集
   *
   * @param sql       查询的SQL语句
   * @param fetchSize 批处理大小
   * @return 结果集包装对象
   */
  protected final StatementResultSet selectTableData(String sql, int fetchSize) {
    if (log.isDebugEnabled()) {
      log.debug("Query table data sql :{}", sql);
    }

    try {
      StatementResultSet srs = new StatementResultSet();
      srs.setConnection(dataSource.getConnection());
      srs.setAutoCommit(srs.getConnection().getAutoCommit());
      srs.getConnection().setAutoCommit(false);
      srs.setStatement(srs.getConnection()
          .createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY));
      srs.getStatement().setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
      srs.getStatement().setFetchSize(fetchSize);
      srs.setResultset(srs.getStatement().executeQuery(sql));
      return srs;
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
  }

  /**
   * 执行写SQL操作
   *
   * @param sql 写SQL语句
   */
  protected final int executeSql(String sql) {
    if (log.isDebugEnabled()) {
      log.debug("Execute sql :{}", sql);
    }
    try (Connection connection = dataSource.getConnection();
        Statement st = connection.createStatement()) {
      return st.executeUpdate(sql);
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

}
