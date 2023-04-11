// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbwriter.mysql;

import com.gitee.dbswitch.common.util.TypeConvertUtils;
import com.gitee.dbswitch.dbwriter.AbstractDatabaseWriter;
import com.gitee.dbswitch.dbwriter.IDatabaseWriter;
import com.gitee.dbswitch.dbwriter.util.ObjectCastUtils;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

/**
 * MySQL数据库写入实现类
 *
 * @author tang
 */
@Slf4j
public class MySqlWriterImpl extends AbstractDatabaseWriter implements IDatabaseWriter {

  private DefaultTransactionDefinition definition;

  public MySqlWriterImpl(DataSource dataSource) {
    super(dataSource);

    this.definition = new DefaultTransactionDefinition();
    this.definition.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
    this.definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
    this.definition.setTimeout(3600);
  }

  @Override
  protected String getDatabaseProductName() {
    return "MySQL";
  }

  @Override
  protected String selectTableMetaDataSqlString(String schemaName, String tableName,
      List<String> fieldNames) {
    if (CollectionUtils.isEmpty(fieldNames)) {
      return String.format("SELECT *  FROM `%s`.`%s`  WHERE 1=2", schemaName, tableName);
    } else {
      return String.format("SELECT `%s`  FROM `%s`.`%s`  WHERE 1=2",
          StringUtils.join(fieldNames, "`,`"), schemaName, tableName);
    }
  }

  @Override
  public long write(List<String> fieldNames, List<Object[]> recordValues) {
    if (recordValues.isEmpty()) {
      return 0;
    }

    recordValues.parallelStream().forEach((Object[] row) -> {
      for (int i = 0; i < row.length; ++i) {
        try {
          row[i] = ObjectCastUtils.castByDetermine(row[i]);
        } catch (Exception e) {
          row[i] = null;
        }
      }
    });

    List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
    String sqlInsert = String.format("INSERT INTO `%s`.`%s` ( `%s` ) VALUES ( %s )",
        schemaName, tableName,
        StringUtils.join(fieldNames, "`,`"),
        StringUtils.join(placeHolders, ","));

    int[] argTypes = new int[fieldNames.size()];
    for (int i = 0; i < fieldNames.size(); ++i) {
      String col = fieldNames.get(i);
      argTypes[i] = this.columnType.get(col);
    }

    PlatformTransactionManager transactionManager = new DataSourceTransactionManager(
        this.dataSource);
    TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager,
        definition);
    Integer ret = transactionTemplate.execute((TransactionStatus transactionStatus) -> {
      try {
        int[] affects = jdbcTemplate.batchUpdate(sqlInsert, recordValues, argTypes);
        int affectCount = Arrays.stream(affects).sum();
        if (log.isDebugEnabled()) {
          log.debug("{} insert data affect count: {}", getDatabaseProductName(), affectCount);
        }
        return affectCount;
      } catch (Throwable t) {
        transactionStatus.setRollbackOnly();
        throw t;
      }
    });

    recordValues.clear();
    if (log.isDebugEnabled()) {
      log.debug("MySQL insert write data  affect count:{}", ret.longValue());
    }

    return ret.longValue();
  }

}
