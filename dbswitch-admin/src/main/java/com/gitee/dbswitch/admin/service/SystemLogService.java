// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.service;

import com.gitee.dbswitch.admin.common.converter.ConverterFactory;
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.controller.converter.SystemLogDetailConverter;
import com.gitee.dbswitch.admin.dao.SystemLogDAO;
import com.gitee.dbswitch.admin.entity.SystemLogEntity;
import com.gitee.dbswitch.admin.model.response.SystemLogDetailResponse;
import com.gitee.dbswitch.admin.type.LogTypeEnum;
import com.gitee.dbswitch.admin.util.PageUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SystemLogService {

  @Resource
  private SystemLogDAO systemLogDAO;

  public PageResult<SystemLogDetailResponse> listAll(Integer type, Integer page, Integer size) {
    LogTypeEnum logType = LogTypeEnum.of(type);
    Supplier<List<SystemLogDetailResponse>> method = () -> {
      List<SystemLogEntity> systemLogEntities = systemLogDAO.listAll(logType);
      return ConverterFactory.getConverter(SystemLogDetailConverter.class).convert(
          systemLogEntities);
    };

    return PageUtils.getPage(method, page, size);
  }

  public Result<SystemLogDetailResponse> getDetailById(Long id) {
    SystemLogEntity systemLogEntity = systemLogDAO.getById(id);
    if (Objects.isNull(systemLogEntity)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "id=" + id);
    }

    return Result.success(ConverterFactory.getConverter(SystemLogDetailConverter.class)
        .convert(systemLogEntity));
  }

}
