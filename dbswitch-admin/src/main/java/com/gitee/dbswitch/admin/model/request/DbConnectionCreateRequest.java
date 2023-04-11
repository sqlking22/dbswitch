// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.model.request;

import com.gitee.dbswitch.admin.entity.DatabaseConnectionEntity;
import com.gitee.dbswitch.admin.type.SupportDbTypeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class DbConnectionCreateRequest {

  private String name;
  private SupportDbTypeEnum type;
  private String version;
  private String driver;
  private String url;
  private String username;
  private String password;

  public DatabaseConnectionEntity toDatabaseConnection() {
    DatabaseConnectionEntity databaseConnectionEntity = new DatabaseConnectionEntity();
    databaseConnectionEntity.setId(null);
    databaseConnectionEntity.setName(name.trim());
    databaseConnectionEntity.setType(type);
    databaseConnectionEntity.setVersion(version.trim());
    databaseConnectionEntity.setDriver(driver.trim());
    databaseConnectionEntity.setUrl(url.trim());
    databaseConnectionEntity.setUsername(username);
    databaseConnectionEntity.setPassword(password);

    return databaseConnectionEntity;
  }

}
