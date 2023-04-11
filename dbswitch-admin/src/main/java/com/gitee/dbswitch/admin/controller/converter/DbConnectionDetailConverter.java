// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.controller.converter;

import com.gitee.dbswitch.admin.common.converter.AbstractConverter;
import com.gitee.dbswitch.admin.entity.DatabaseConnectionEntity;
import com.gitee.dbswitch.admin.model.response.DbConnectionDetailResponse;
import java.util.Objects;

public class DbConnectionDetailConverter extends
    AbstractConverter<DatabaseConnectionEntity, DbConnectionDetailResponse> {

  @Override
  public DbConnectionDetailResponse convert(DatabaseConnectionEntity databaseConnectionEntity) {
    if (Objects.isNull(databaseConnectionEntity)) {
      return null;
    }

    DbConnectionDetailResponse response = new DbConnectionDetailResponse();
    response.setId(databaseConnectionEntity.getId());
    response.setName(databaseConnectionEntity.getName());
    response.setType(databaseConnectionEntity.getType());
    response.setVersion(databaseConnectionEntity.getVersion());
    response.setDriver(databaseConnectionEntity.getDriver());
    response.setUrl(databaseConnectionEntity.getUrl());
    response.setUsername(databaseConnectionEntity.getUsername());
    response.setPassword(databaseConnectionEntity.getPassword());
    response.setCreateTime(databaseConnectionEntity.getCreateTime());
    response.setUpdateTime(databaseConnectionEntity.getUpdateTime());

    return response;
  }
}
