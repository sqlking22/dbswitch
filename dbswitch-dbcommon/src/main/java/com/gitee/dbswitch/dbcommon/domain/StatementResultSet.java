// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbcommon.domain;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

/**
 * JDBC连接及结果集实体参数定义类
 *
 * @author tang
 */
@Slf4j
@Data
public class StatementResultSet implements AutoCloseable {

  private boolean isAutoCommit;
  private Connection connection;
  private Statement statement;
  private ResultSet resultset;

  @Override
  public void close() {
    try {
      connection.setAutoCommit(isAutoCommit);
    } catch (SQLException e) {
      log.warn("Jdbc Connect setAutoCommit() failed, error: {}", e.getMessage());
    }

    JdbcUtils.closeResultSet(resultset);
    JdbcUtils.closeStatement(statement);
    JdbcUtils.closeConnection(connection);
  }
}
