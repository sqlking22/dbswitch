// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.type;

import java.util.Arrays;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
public enum SupportDbTypeEnum {

  MYSQL(1, "mysql", "com.mysql.jdbc.Driver", 3306,
      "/* ping */ SELECT 1",
      "jdbc:mysql://",
      new String[]{"jdbc:mysql://{host}[:{port}]/[{database}][\\?{params}]"}),
  MARIADB(2, "mariadb", "org.mariadb.jdbc.Driver", 3306,
      "SELECT 1",
      "jdbc:mariadb://",
      new String[]{"jdbc:mariadb://{host}[:{port}]/[{database}][\\?{params}]"}),
  ORACLE(3, "oracle", "oracle.jdbc.driver.OracleDriver", 1521,
      "SELECT 'Hello' from DUAL",
      "jdbc:oracle:thin:@",
      new String[]{"jdbc:oracle:thin:@{host}:{port}:{database}",
          "jdbc:oracle:thin:@//{host}[:{port}]/{database}"}),
  SQLSERVER(4, "sqlserver", "com.microsoft.sqlserver.jdbc.SQLServerDriver", 1433,
      "SELECT 1+2 as a",
      "jdbc:sqlserver://",
      new String[]{"jdbc:sqlserver://{host}[:{port}][;DatabaseName={database}][;{params}]"}),
  POSTGRESQL(5, "postgresql", "org.postgresql.Driver", 5432,
      "SELECT 1",
      "jdbc:postgresql://",
      new String[]{"jdbc:postgresql://{host}[:{port}]/[{database}][\\?{params}]"}),
  DB2(6, "db2", "com.ibm.db2.jcc.DB2Driver", 50000,
      "SELECT 1 FROM SYSIBM.SYSDUMMY1",
      "jdbc:db2://",
      new String[]{"jdbc:db2://{host}:{port}/{database}[:{params}]"}),
  DM(7, "dm", "dm.jdbc.driver.DmDriver", 5236,
      "SELECT 'Hello' from DUAL",
      "jdbc:dm://",
      new String[]{"jdbc:dm://{host}:{port}[/{database}][\\?{params}]"}),
  KINGBASE(8, "kingbase", "com.kingbase8.Driver", 54321,
      "SELECT 1",
      "jdbc:kingbase8://",
      new String[]{"jdbc:kingbase8://{host}[:{port}]/[{database}][\\?{params}]"}),
  OSCAR(9, "oscar", "com.oscar.Driver", 2003,
      "SELECT 1",
      "jdbc:oscar://",
      new String[]{"jdbc:oscar://{host}[:{port}]/[{database}][\\?{params}]"}),
  GBASE8A(10, "gbase8a", "com.gbase.jdbc.Driver", 5258,
      "/* ping */ SELECT 1",
      "jdbc:gbase://",
      new String[]{"jdbc:gbase://{host}[:{port}]/[{database}][\\?{params}]"}),
  SYBASE(11, "sybase", "com.sybase.jdbc4.jdbc.SybDriver", 5000,
      "SELECT 1+2 as a",
      "jdbc:sybase:Tds:",
      new String[]{"jdbc:sybase:Tds:{host}[:{port}][/{database}][\\?{params}]"}),
  HIVE(12, "hive", "org.apache.hive.jdbc.HiveDriver", 10000,
      "SELECT 1",
      "jdbc:hive2://",
      new String[]{"jdbc:hive2://{host}[:{port}]/[{database}][\\?{params}]"}),
  // 参考文章：https://blog.csdn.net/wank1259162/article/details/104946744
  SQLITE3(13, "sqlite3", "org.sqlite.JDBC", 0,
      "SELECT 1",
      "jdbc:sqlite:",
      new String[]{"jdbc:sqlite:{file}", "jdbc:sqlite::resource:{file}"}),
  ;

  private int id;
  private String name;
  private String driver;
  private int port;
  private String sql;
  private String urlPrefix;
  private String[] url;

  public boolean hasDatabaseName() {
    return !Arrays.asList(DM, SQLITE3).contains(this);
  }

  public boolean hasFilePath() {
    return this == SQLITE3;
  }

  public boolean hasAddress() {
    return this != SQLITE3;
  }

  public static boolean exists(String name) {
    return Arrays.stream(values()).anyMatch(item -> item.name().equalsIgnoreCase(name));
  }

  public static SupportDbTypeEnum of(String name) {
    if (!StringUtils.isEmpty(name)) {
      for (SupportDbTypeEnum type : SupportDbTypeEnum.values()) {
        if (type.getName().equalsIgnoreCase(name)) {
          return type;
        }
      }
    }

    throw new IllegalArgumentException("cannot find enum name: " + name);
  }

  public static boolean isUnsupportedTargetSqlite(String url) {
    String prefix1 = "jdbc:sqlite::resource:";
    //String prefix2 = "jdbc:sqlite::memory:";
    return url.startsWith(prefix1);
  }

}
