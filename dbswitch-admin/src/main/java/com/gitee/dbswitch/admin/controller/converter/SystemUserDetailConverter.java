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
import com.gitee.dbswitch.admin.entity.SystemUserEntity;
import com.gitee.dbswitch.admin.model.response.SystemUserDetailResponse;
import java.util.Objects;

public class SystemUserDetailConverter extends
    AbstractConverter<SystemUserEntity, SystemUserDetailResponse> {

  @Override
  public SystemUserDetailResponse convert(SystemUserEntity user) {
    if (Objects.isNull(user)) {
      return null;
    }

    SystemUserDetailResponse response = new SystemUserDetailResponse();
    response.setId(user.getId());
    response.setUsername(user.getUsername());
    response.setRealName(user.getRealName());
    response.setEmail(user.getEmail());
    response.setAddress(user.getAddress());
    response.setLocked(user.getLocked());
    response.setCreateTime(user.getCreateTime());
    response.setUpdateTime(user.getUpdateTime());

    return response;
  }
}
