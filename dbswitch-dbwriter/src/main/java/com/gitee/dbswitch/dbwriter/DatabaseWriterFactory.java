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

import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.DatabaseAwareUtils;
import com.gitee.dbswitch.dbwriter.db2.DB2WriterImpl;
import com.gitee.dbswitch.dbwriter.dm.DmWriterImpl;
import com.gitee.dbswitch.dbwriter.gpdb.GreenplumCopyWriterImpl;
import com.gitee.dbswitch.dbwriter.kingbase.KingbaseInsertWriterImpl;
import com.gitee.dbswitch.dbwriter.mssql.SqlServerWriterImpl;
import com.gitee.dbswitch.dbwriter.mysql.MySqlWriterImpl;
import com.gitee.dbswitch.dbwriter.oracle.OracleWriterImpl;
import com.gitee.dbswitch.dbwriter.oscar.OscarWriterImpl;
import com.gitee.dbswitch.dbwriter.sqlite.Sqlite3WriterImpl;
import com.gitee.dbswitch.dbwriter.sybase.SybaseWriterImpl;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.sql.DataSource;

/**
 * 数据库写入器构造工厂类
 *
 * @author tang
 */
public class DatabaseWriterFactory {

  private static final Map<ProductTypeEnum, Function<DataSource, IDatabaseWriter>> DATABASE_WRITER_MAPPER
      = new HashMap<ProductTypeEnum, Function<DataSource, IDatabaseWriter>>() {

    private static final long serialVersionUID = 3365136872693503697L;

    {
      put(ProductTypeEnum.MYSQL, MySqlWriterImpl::new);
      put(ProductTypeEnum.MARIADB, MySqlWriterImpl::new);
      put(ProductTypeEnum.ORACLE, OracleWriterImpl::new);
      put(ProductTypeEnum.SQLSERVER, SqlServerWriterImpl::new);
      put(ProductTypeEnum.SQLSERVER2000, SqlServerWriterImpl::new);
      put(ProductTypeEnum.POSTGRESQL, GreenplumCopyWriterImpl::new);
      put(ProductTypeEnum.GREENPLUM, GreenplumCopyWriterImpl::new);
      put(ProductTypeEnum.DB2, DB2WriterImpl::new);
      put(ProductTypeEnum.DM, DmWriterImpl::new);
      put(ProductTypeEnum.SYBASE, SybaseWriterImpl::new);
      //对于kingbase当前只能使用insert模式
      put(ProductTypeEnum.KINGBASE, KingbaseInsertWriterImpl::new);
      put(ProductTypeEnum.OSCAR, OscarWriterImpl::new);
      put(ProductTypeEnum.GBASE8A, MySqlWriterImpl::new);
      put(ProductTypeEnum.SQLITE3, Sqlite3WriterImpl::new);
    }
  };

  /**
   * 获取指定数据库类型的写入器
   *
   * @param dataSource 连接池数据源
   * @return 写入器对象
   */
  public static IDatabaseWriter createDatabaseWriter(DataSource dataSource) {
    return DatabaseWriterFactory.createDatabaseWriter(dataSource, false);
  }

  /**
   * 获取指定数据库类型的写入器
   *
   * @param dataSource 连接池数据源
   * @param insert     对于GP/GP数据库来说是否使用insert引擎写入
   * @return 写入器对象
   */
  public static IDatabaseWriter createDatabaseWriter(DataSource dataSource, boolean insert) {
    ProductTypeEnum type = DatabaseAwareUtils.getDatabaseTypeByDataSource(dataSource);
    if (insert) {
      if (ProductTypeEnum.POSTGRESQL.equals(type) || ProductTypeEnum.GREENPLUM.equals(type)) {
        return new com.gitee.dbswitch.dbwriter.gpdb.GreenplumInsertWriterImpl(dataSource);
      }
    }

    if (!DATABASE_WRITER_MAPPER.containsKey(type)) {
      throw new RuntimeException(
          String.format("[dbwrite] Unsupported database type (%s)", type));
    }

    return DATABASE_WRITER_MAPPER.get(type).apply(dataSource);
  }

}
