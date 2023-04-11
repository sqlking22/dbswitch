// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.core.database.impl;

import com.gitee.dbswitch.common.constant.Const;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.core.database.AbstractDatabase;
import com.gitee.dbswitch.core.database.IDatabaseInterface;
import com.gitee.dbswitch.core.model.ColumnDescription;
import com.gitee.dbswitch.core.model.ColumnMetaData;
import com.gitee.dbswitch.core.model.TableDescription;
import com.gitee.dbswitch.core.util.DDLFormatterUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 支持SQLite数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseSqliteImpl extends AbstractDatabase implements IDatabaseInterface {

  public DatabaseSqliteImpl() {
    super("org.sqlite.JDBC");
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.SQLITE3;
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    return Collections.singletonList("main");
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    String sql = "SELECT sql FROM \"sqlite_master\" where type='table' and tbl_name=? ";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs != null && rs.next()) {
          return DDLFormatterUtils.format(rs.getString(1));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return "";
  }

  @Override
  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    String sql = "SELECT sql FROM \"sqlite_master\" where type='view' and tbl_name=? ";
    try (PreparedStatement ps = connection.prepareStatement(sql)) {
      ps.setString(1, tableName);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs != null && rs.next()) {
          return DDLFormatterUtils.format(rs.getString(1));
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return "";
  }

  @Override
  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    String querySQL = String.format(" %s LIMIT 0 ", sql.replace(";", ""));
    return this.getSelectSqlColumnMeta(connection, querySQL);
  }

  @Override
  protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
    return String.format("SELECT * FROM \"%s\".\"%s\"  ", schemaName, tableName);
  }

  @Override
  protected String getTestQuerySQL(String sql) {
    return String.format("explain %s", sql.replace(";", ""));
  }

  @Override
  public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc,
      boolean addCr, boolean withRemarks) {
    String fieldname = v.getName();
    int length = v.getLength();
    int precision = v.getPrecision();
    int type = v.getType();

    String retval = " \"" + fieldname + "\"   ";

    switch (type) {
      case ColumnMetaData.TYPE_TIMESTAMP:
      case ColumnMetaData.TYPE_TIME:
      case ColumnMetaData.TYPE_DATE:
        // sqlite中没有时间数据类型
        retval += "DATETIME";
        break;
      case ColumnMetaData.TYPE_BOOLEAN:
        retval += "CHAR(1)";
        break;
      case ColumnMetaData.TYPE_NUMBER:
      case ColumnMetaData.TYPE_INTEGER:
      case ColumnMetaData.TYPE_BIGNUMBER:
        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          // 关键字 AUTOINCREMENT 只能⽤于整型（INTEGER）字段。
          if (useAutoInc) {
            retval += "INTEGER PRIMARY KEY AUTOINCREMENT";
          } else {
            retval += "BIGINT ";
          }
        } else {
          if (precision != 0 || length < 0 || length > 18) {
            retval += "NUMERIC";
          } else {
            retval += "INTEGER";
          }
        }
        break;
      case ColumnMetaData.TYPE_STRING:
        if (length < 1 || length >= AbstractDatabase.CLOB_LENGTH) {
          retval += "BLOB";
        } else {
          retval += "TEXT";
        }
        break;
      case ColumnMetaData.TYPE_BINARY:
        retval += "BLOB";
        break;
      default:
        retval += "TEXT";
        break;
    }

    if (addCr) {
      retval += Const.CR;
    }

    return retval;
  }

  @Override
  public List<String> getTableColumnCommentDefinition(TableDescription td,
      List<ColumnDescription> cds) {
    return Collections.emptyList();
  }

}
