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

import com.gitee.dbswitch.admin.common.annotation.TokenCheck;
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.config.SwaggerConfig;
import com.gitee.dbswitch.admin.model.response.SystemLogDetailResponse;
import com.gitee.dbswitch.admin.service.SystemLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"审计日志接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/syslog")
public class SystemLogController {

  @Resource
  private SystemLogService systemLogService;

  @TokenCheck
  @ApiOperation(value = "日志列表")
  @GetMapping(value = "/list/{type}/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResult<SystemLogDetailResponse> listAll(
      @PathVariable("type") Integer type,
      @PathVariable("page") Integer page,
      @PathVariable("size") Integer size) {
    return systemLogService.listAll(type, page, size);
  }

  @TokenCheck
  @ApiOperation(value = "日志详情")
  @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<SystemLogDetailResponse> getDetail(
      @PathVariable("id") Long id) {
    return systemLogService.getDetailById(id);
  }

}
