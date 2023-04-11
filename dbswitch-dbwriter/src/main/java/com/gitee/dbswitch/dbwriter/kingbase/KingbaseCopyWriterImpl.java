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
import com.gitee.dbswitch.dbwriter.gpdb.GreenplumCopyWriterImpl;
import javax.sql.DataSource;

/**
 * Kingbase8数据库Copy写入实现类
 *
 * @author tang
 */
public class KingbaseCopyWriterImpl extends GreenplumCopyWriterImpl implements IDatabaseWriter {

  public KingbaseCopyWriterImpl(DataSource dataSource) {
    super(dataSource);
  }

  @Override
  protected String getDatabaseProductName() {
    return "Kingbase";
  }

}
