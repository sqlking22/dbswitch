// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.type;

import java.util.Arrays;

/**
 * 数据库产品类型的枚举定义
 *
 * @author Tang
 */
public enum ProductTypeEnum {
  /**
   * 未知数据库类型
   */
  UNKNOWN(0),

  /**
   * MySQL数据库类型
   */
  MYSQL(1),

  /**
   * Oracle数据库类型
   */
  ORACLE(2),

  /**
   * SQLServer 2000数据库类型
   */
  SQLSERVER2000(3),

  /**
   * SQLServer数据库类型
   */
  SQLSERVER(4),

  /**
   * PostgreSQL数据库类型
   */
  POSTGRESQL(5),

  /**
   * Greenplum数据库类型
   */
  GREENPLUM(6),

  /**
   * MariaDB数据库类型
   */
  MARIADB(7),

  /**
   * DB2数据库类型
   */
  DB2(8),

  /**
   * [国产]达梦数据库类型
   */
  DM(9),

  /**
   * [国产]人大金仓数据库类型
   */
  KINGBASE(10),

  /**
   * [国产]神通数据库
   */
  OSCAR(11),

  /**
   * [国产]南大通用GBase8a数据库
   */
  GBASE8A(12),

  /**
   * HIVE数据库
   */
  HIVE(13),

  /**
   * SQLite数据库
   */
  SQLITE3(14),

  /**
   * Sybase数据库类型
   */
  SYBASE(15),
  ;

  private int index;

  ProductTypeEnum(int idx) {
    this.index = idx;
  }

  public int getIndex() {
    return index;
  }

  public boolean noCommentStatement() {
    return Arrays.asList(
        ProductTypeEnum.MYSQL,
        ProductTypeEnum.MARIADB,
        ProductTypeEnum.GBASE8A,
        ProductTypeEnum.HIVE,
        ProductTypeEnum.SQLITE3,
        ProductTypeEnum.SYBASE
    ).contains(this);
  }

}
