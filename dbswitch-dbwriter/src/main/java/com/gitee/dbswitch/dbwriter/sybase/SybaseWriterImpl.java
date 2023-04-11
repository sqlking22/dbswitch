// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbwriter.sybase;

import com.gitee.dbswitch.dbwriter.mssql.SqlServerWriterImpl;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * Sybase批量写入实现类
 *
 * @author tang
 */
@Slf4j
public class SybaseWriterImpl extends SqlServerWriterImpl {

  public SybaseWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Sybase";
  }

}
