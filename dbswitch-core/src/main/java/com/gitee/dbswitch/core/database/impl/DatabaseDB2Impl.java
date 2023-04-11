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
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;

/**
 * 支持DB2数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseDB2Impl extends AbstractDatabase implements IDatabaseInterface {

  private static final String CALL_DB2LK_GEN =
      "CALL SYSPROC.DB2LK_GENERATE_DDL(?,?)";
  private static final String CALL_DB2LK_CLEAN =
      "CALL SYSPROC.DB2LK_CLEAN_TABLE(?)";
  private static final String SEL_DB2LK =
      "SELECT SQL_STMT FROM SYSTOOLS.DB2LOOK_INFO WHERE OP_TOKEN = '%d' ORDER BY OP_SEQUENCE WITH UR";
  private static final String DB2LK_COMMAND =
      "-e -x -xd -td %s -t %s";
  private static final String SHOW_CREATE_VIEW_SQL =
      "SELECT TEXT FROM SYSCAT.VIEWS WHERE VIEWSCHEMA ='%s' AND VIEWNAME ='%s'";

  public DatabaseDB2Impl() {
    super("com.ibm.db2.jcc.DB2Driver");
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.DB2;
  }

  @Override
  public String getTableDDL(Connection connection, String schemaName, String tableName) {
    String fullName = String.format("\"%s\".\"%s\"", schemaName, tableName);
    final String command = String.format(DB2LK_COMMAND, "\n", fullName);
    List<String> result = new ArrayList<>();
    try (CallableStatement stmt = connection.prepareCall(CALL_DB2LK_GEN)) {
      stmt.registerOutParameter(2, java.sql.Types.INTEGER);
      stmt.setString(1, command);
      stmt.execute();
      int token = stmt.getInt(2);
      String sql = String.format(SEL_DB2LK, token);
      try (Statement st = connection.createStatement()) {
        if (st.execute(sql)) {
          try (ResultSet rs = st.getResultSet()) {
            if (rs != null) {
              while (rs.next()) {
                String value = rs.getString(1);
                Optional.ofNullable(value).ifPresent(result::add);
              }
            }
          }
        }
      } finally {
        try (CallableStatement st = connection.prepareCall(CALL_DB2LK_CLEAN)) {
          st.setInt(1, token);
          st.execute();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return String.join(";", result);
  }

  @Override
  public String getViewDDL(Connection connection, String schemaName, String tableName) {
    String sql = String.format(SHOW_CREATE_VIEW_SQL, schemaName, tableName);
    try (Statement st = connection.createStatement()) {
      if (st.execute(sql)) {
        try (ResultSet rs = st.getResultSet()) {
          if (rs != null && rs.next()) {
            return rs.getString(1);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

    return null;
  }

  @Override
  public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
    String querySQL = String.format(" %s fetch first 1 rows only ", sql.replace(";", ""));
    return this.getSelectSqlColumnMeta(connection, querySQL);
  }

  @Override
  protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
    return String.format("SELECT * FROM \"%s\".\"%s\"  ", schemaName, tableName);
  }

  @Override
  protected String getTestQuerySQL(String sql) {
    return String.format("SELECT * FROM ( %s ) t WHERE 1=2 ", sql.replace(";", ""));
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
        retval += "TIMESTAMP";
        break;
      case ColumnMetaData.TYPE_TIME:
        retval += "TIME";
        break;
      case ColumnMetaData.TYPE_DATE:
        retval += "DATE";
        break;
      case ColumnMetaData.TYPE_BOOLEAN:
        retval += "BOOLEAN";
        break;
      case ColumnMetaData.TYPE_NUMBER:
      case ColumnMetaData.TYPE_BIGNUMBER:
        if (null != pks && pks.contains(fieldname)) {
          if (useAutoInc) {
            retval += "BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NOCACHE)";
          } else {
            retval += "BIGINT NOT NULL";
          }
        } else {
          if (length > 0) {
            retval += "DECIMAL(" + length;
            if (precision > 0) {
              retval += ", " + precision;
            }
            retval += ")";
          } else {
            retval += "FLOAT";
          }
        }
        break;
      case ColumnMetaData.TYPE_INTEGER:
        if (null != pks && pks.contains(fieldname)) {
          if (useAutoInc) {
            retval += "INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 0, INCREMENT BY 1, NOCACHE)";
          } else {
            retval += "INTEGER NOT NULL";
          }
        } else {
          retval += "INTEGER";
        }
        break;
      case ColumnMetaData.TYPE_STRING:
        if (length * 3 > 32672) {
          retval += "CLOB";
        } else {
          retval += "VARCHAR";
          if (length > 0) {
            retval += "(" + length * 3;
          } else {
            retval += "(";
          }

          retval += ")";
        }

        if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
          retval += " NOT NULL";
        }

        break;
      case ColumnMetaData.TYPE_BINARY:
        if (length > 32672) {
          retval += "BLOB(" + length + ")";
        } else {
          if (length > 0) {
            retval += "CHAR(" + length + ") FOR BIT DATA";
          } else {
            retval += "BLOB";
          }
        }
        break;
      default:
        retval += "CLOB";
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
    List<String> results = new ArrayList<>();
    if (StringUtils.isNotBlank(td.getRemarks())) {
      results.add(String
          .format("COMMENT ON TABLE \"%s\".\"%s\" IS '%s' ",
              td.getSchemaName(), td.getTableName(),
              td.getRemarks().replace("\"", "\\\"")));
    }

    for (ColumnDescription cd : cds) {
      if (StringUtils.isNotBlank(cd.getRemarks())) {
        results.add(String
            .format("COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s' ",
                td.getSchemaName(), td.getTableName(), cd.getFieldName(),
                cd.getRemarks().replace("\"", "\\\"")));
      }
    }

    return results;
  }

}
