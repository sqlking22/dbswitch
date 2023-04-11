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
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.controller.converter.SystemUserDetailConverter;
import com.gitee.dbswitch.admin.dao.SystemUserDAO;
import com.gitee.dbswitch.admin.entity.SystemUserEntity;
import com.gitee.dbswitch.admin.model.response.SystemUserDetailResponse;
import com.gitee.dbswitch.admin.util.PasswordUtils;
import com.gitee.dbswitch.admin.util.ServletUtils;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SystemUserService {

  @Resource
  private SystemUserDAO systemUserDAO;

  public Result<SystemUserDetailResponse> getUserDetailById(Long id) {
    SystemUserEntity user = systemUserDAO.getById(id);
    if (Objects.isNull(user)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "id=" + id);
    }

    return Result.success(ConverterFactory.getConverter(SystemUserDetailConverter.class)
        .convert(user));
  }

  public Result<SystemUserDetailResponse> getUserDetailByUsername(String username) {
    SystemUserEntity user = findByUsername(username);
    if (Objects.isNull(user)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "username=" + username);
    }

    return Result.success(ConverterFactory.getConverter(SystemUserDetailConverter.class)
        .convert(user));
  }

  public Result changeOwnPassword(String oldPassword, String newPassword) {
    String username = ServletUtils.getHttpServletRequest().getAttribute("username").toString();
    SystemUserEntity systemUserEntity = findByUsername(username);
    if (Objects.isNull(systemUserEntity)) {
      return Result.failed(ResultCode.ERROR_USER_NOT_EXISTS, username);
    }

    String encryptOldPassword = PasswordUtils
        .encryptPassword(oldPassword, systemUserEntity.getSalt());
    if (!encryptOldPassword.equals(systemUserEntity.getPassword())) {
      return Result.failed(ResultCode.ERROR_USER_PASSWORD_WRONG, username);
    }

    String encryptNewPassword = PasswordUtils
        .encryptPassword(newPassword, systemUserEntity.getSalt());
    systemUserDAO.updateUserPassword(username, encryptNewPassword);

    return Result.success();
  }

  public SystemUserEntity findByUsername(String username) {
    return systemUserDAO.findByUsername(username);
  }

}
