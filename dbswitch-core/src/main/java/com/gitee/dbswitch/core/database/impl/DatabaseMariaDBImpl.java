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

import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.JDBCURL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

/**
 * 支持MariaDB数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseMariaDBImpl extends DatabaseMysqlImpl {

  public DatabaseMariaDBImpl() {
    super("org.mariadb.jdbc.Driver");
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.MARIADB;
  }

  @Override
  public List<String> querySchemaList(Connection connection) {
    try {
      final Matcher matcher = JDBCURL
          .getPattern("jdbc:mariadb://{host}[:{port}]/[{database}][\\?{params}]")
          .matcher(connection.getMetaData().getURL());
      if (matcher.matches()) {
        return Collections.singletonList(matcher.group("database"));
      }
      throw new RuntimeException("get database name from jdbc url failed!");
    } catch (SQLException e) {
      throw new RuntimeException(e);
    } catch (RuntimeException e) {
      throw e;
    }
  }

}
