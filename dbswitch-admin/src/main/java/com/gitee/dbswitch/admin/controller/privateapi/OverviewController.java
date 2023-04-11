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
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.config.SwaggerConfig;
import com.gitee.dbswitch.admin.model.ops.OpsTaskJobTrend;
import com.gitee.dbswitch.admin.model.response.OverviewStatisticsResponse;
import com.gitee.dbswitch.admin.service.OverviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"统计概览接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/overview")
public class OverviewController {

  @Resource
  private OverviewService overviewService;

  @TokenCheck
  @ApiOperation(value = "统计概览")
  @GetMapping(value = "/statistics", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<OverviewStatisticsResponse> overviewStatistics() {
    return Result.success(overviewService.statistics());
  }

  @TokenCheck
  @ApiOperation(value = "执行趋势")
  @GetMapping(value = "/trend/{days}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<List<OpsTaskJobTrend>> trend(@PathVariable("days") Integer days) {
    return Result.success(overviewService.trend(days));
  }

}
