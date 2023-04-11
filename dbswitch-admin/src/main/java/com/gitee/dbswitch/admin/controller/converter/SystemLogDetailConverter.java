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
import com.gitee.dbswitch.admin.entity.SystemLogEntity;
import com.gitee.dbswitch.admin.model.response.SystemLogDetailResponse;
import java.util.Objects;

public class SystemLogDetailConverter extends
    AbstractConverter<SystemLogEntity, SystemLogDetailResponse> {

  @Override
  public SystemLogDetailResponse convert(SystemLogEntity systemLogEntity) {
    if (Objects.isNull(systemLogEntity)) {
      return null;
    }

    SystemLogDetailResponse response = new SystemLogDetailResponse();
    response.setId(systemLogEntity.getId());
    response.setUsername(systemLogEntity.getUsername());
    response.setIpAddress(systemLogEntity.getIpAddress());
    response.setModuleName(systemLogEntity.getModuleName());
    response.setContent(systemLogEntity.getContent());
    response.setUrlPath(systemLogEntity.getUrlPath());
    response.setUserAgent(systemLogEntity.getUserAgent());
    response.setFailed(systemLogEntity.getFailed());
    response.setException(systemLogEntity.getException());
    response.setElapseSeconds(systemLogEntity.getElapseSeconds());
    response.setCreateTime(systemLogEntity.getCreateTime());

    return response;
  }

}
