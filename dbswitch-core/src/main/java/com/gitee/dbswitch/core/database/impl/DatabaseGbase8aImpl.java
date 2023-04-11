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

/**
 * 支持GBase8a数据库的元信息实现
 *
 * @author tang
 */
public class DatabaseGbase8aImpl extends DatabaseMysqlImpl {

  public DatabaseGbase8aImpl() {
    super("com.gbase.jdbc.Driver");
  }

  @Override
  public ProductTypeEnum getDatabaseType() {
    return ProductTypeEnum.GBASE8A;
  }

}