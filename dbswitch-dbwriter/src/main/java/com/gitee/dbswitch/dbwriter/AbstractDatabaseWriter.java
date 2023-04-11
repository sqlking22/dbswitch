// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbwriter;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

/**
 * 数据库写入抽象基类
 *
 * @author tang
 */
@Slf4j
public abstract class AbstractDatabaseWriter implements IDatabaseWriter {

  protected DataSource dataSource;
  protected JdbcTemplate jdbcTemplate;
  protected String schemaName;
  protected String tableName;
  protected Map<String, Integer> columnType;

  public AbstractDatabaseWriter(DataSource dataSource) {
    this.dataSource = dataSource;
    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
    this.schemaName = null;
    this.tableName = null;
    this.columnType = null;
  }

  @Override
  public DataSource getDataSource() {
    return this.dataSource;
  }

  @Override
  public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
    String sql = this.selectTableMetaDataSqlString(schemaName, tableName, fieldNames);
    Map<String, Integer> columnMetaData = new HashMap<>();
    jdbcTemplate.execute((Connection conn) -> {
      try (Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sql);) {
        ResultSetMetaData rsMetaData = rs.getMetaData();
        for (int i = 0, len = rsMetaData.getColumnCount(); i < len; i++) {
          columnMetaData.put(rsMetaData.getColumnName(i + 1), rsMetaData.getColumnType(i + 1));
        }

        return true;
      } catch (Exception e) {
        throw new RuntimeException(
            String.format("获取表:%s.%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.", schemaName, tableName), e);
      }
    });

    this.schemaName = schemaName;
    this.tableName = tableName;
    this.columnType = Objects.requireNonNull(columnMetaData);
    if (this.columnType.isEmpty()) {
      throw new RuntimeException(
          String.format("获取表:%s.%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.", schemaName, tableName));
    }

  }

  protected String selectTableMetaDataSqlString(String schemaName, String tableName,
      List<String> fieldNames) {
    if (CollectionUtils.isEmpty(fieldNames)) {
      return String.format("SELECT *  FROM \"%s\".\"%s\"  WHERE 1=2", schemaName, tableName);
    } else {
      return String.format("SELECT \"%s\"  FROM \"%s\".\"%s\"  WHERE 1=2",
          StringUtils.join(fieldNames, "\",\""), schemaName, tableName);
    }
  }

  protected abstract String getDatabaseProductName();

  protected TransactionDefinition getTransactionDefinition() {
    DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
    definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    return definition;
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    if (recordValues.isEmpty()) {
      return 0;
    }

    String sqlInsert = String.format("INSERT INTO \"%s\".\"%s\" ( \"%s\" ) VALUES ( %s )",
        schemaName, tableName,
        StringUtils.join(fieldNames, "\",\""),
        StringUtils.join(Collections.nCopies(fieldNames.size(), "?"), ","));

    int[] argTypes = new int[fieldNames.size()];
    for (int i = 0; i < fieldNames.size(); ++i) {
      String col = fieldNames.get(i);
      argTypes[i] = this.columnType.get(col);
    }

    PlatformTransactionManager transactionManager = new DataSourceTransactionManager(
        this.dataSource);
    TransactionStatus status = transactionManager.getTransaction(getTransactionDefinition());

    try {
      //int[] affects = jdbcTemplate.batchUpdate(sqlInsert, recordValues, argTypes);
      //int affectCount = Arrays.stream(affects).sum();
      jdbcTemplate.batchUpdate(sqlInsert, recordValues, argTypes);
      transactionManager.commit(status);
      int affectCount = recordValues.size();
      recordValues.clear();
      if (log.isDebugEnabled()) {
        log.debug("{} insert data affect count: {}", getDatabaseProductName(), affectCount);
      }
      return affectCount;
    } catch (TransactionException e) {
      transactionManager.rollback(status);
      throw e;
    } catch (Exception e) {
      transactionManager.rollback(status);
      throw e;
    }
  }

}
