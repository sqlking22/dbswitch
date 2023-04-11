// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.controller.privateapi;

import com.gitee.dbswitch.admin.common.annotation.LogOperate;
import com.gitee.dbswitch.admin.common.annotation.TokenCheck;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.config.SwaggerConfig;
import com.gitee.dbswitch.admin.model.response.SystemUserDetailResponse;
import com.gitee.dbswitch.admin.service.SystemUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"用户管理接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/user")
public class UserController {

  @Resource
  private SystemUserService systemUserService;

  @TokenCheck
  @ApiOperation(value = "用户详情", notes = "根据用户ID获取用户详情")
  @GetMapping(value = "/detail/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<SystemUserDetailResponse> getUserById(@PathVariable("id") Long id) {
    return systemUserService.getUserDetailById(id);
  }

  @TokenCheck
  @ApiOperation(value = "用户详情", notes = "根据用户名获取用户详情")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "username", value = "账号登陆名", required = true, dataTypeClass = String.class)
  })
  @GetMapping(value = "/detail/name", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<SystemUserDetailResponse> getUserByName(@RequestParam("username") String username) {
    return systemUserService.getUserDetailByUsername(username);
  }

  @TokenCheck
  @LogOperate(name = "修改密码", description = "用户修改自己的密码")
  @RequestMapping(value = "/changePassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  @ApiOperation(value = "修改密码", notes = "用户修改自己的密码")
  @ApiImplicitParams({
      @ApiImplicitParam(paramType = "query", name = "oldPassword", value = "旧密码", required = true, dataTypeClass = String.class),
      @ApiImplicitParam(paramType = "query", name = "newPassword", value = "新密码", required = true, dataTypeClass = String.class)
  })
  public Result changeOwnPassword(@RequestParam("oldPassword") String oldPassword,
      @RequestParam("newPassword") String newPassword) {
    return systemUserService.changeOwnPassword(oldPassword, newPassword);
  }

}
