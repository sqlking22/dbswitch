// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.dbwriter.kingbase;

import com.gitee.dbswitch.dbwriter.IDatabaseWriter;
import com.gitee.dbswitch.dbwriter.gpdb.GreenplumInsertWriterImpl;
import javax.sql.DataSource;

/**
 * Kingbase8数据库Insert写入实现类
 *
 * @author tang
 */
public class KingbaseInsertWriterImpl extends GreenplumInsertWriterImpl implements IDatabaseWriter {

  public KingbaseInsertWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Kingbase";
  }

}
