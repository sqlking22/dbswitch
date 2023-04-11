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
import com.gitee.dbswitch.admin.model.response.TaskJobDetailResponse;
import com.gitee.dbswitch.admin.model.response.TaskJobLogbackResponse;
import com.gitee.dbswitch.admin.service.JobLogbackService;
import com.gitee.dbswitch.admin.service.JobManagerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"作业管理接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/ops")
public class JobManagerController {

  @Resource
  private JobManagerService opsManagerService;
  @Resource
  private JobLogbackService jobLogbackService;

  @TokenCheck
  @ApiOperation(value = "根据任务ID查询作业执行记录")
  @GetMapping(value = "/jobs/list/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResult<TaskJobDetailResponse> listJobs(@RequestParam("id") Long id,
      @PathVariable("page") Integer page,
      @PathVariable("size") Integer size) {
    return opsManagerService.listJobs(id, page, size);
  }

  @TokenCheck
  @ApiOperation(value = "根据作业的ID查询执行记录")
  @GetMapping(value = "/job/detail", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<TaskJobDetailResponse> detailJob(@RequestParam("id") Long id) {
    return opsManagerService.detailJob(id);
  }

  @TokenCheck
  @ApiOperation(value = "根据作业的ID取消JOB作业")
  @GetMapping(value = "/job/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<Boolean> cancelJob(@RequestParam("id") Long id) {
    return opsManagerService.cancelJob(id);
  }

  @TokenCheck
  @ApiOperation(value = "根据作业的ID查询最后N条日志")
  @GetMapping(value = "/job/logs/tail", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<TaskJobLogbackResponse> tailLogs(@RequestParam("id") Long id, @RequestParam("size") Integer size) {
    return jobLogbackService.tailLog(id, size);
  }

  @TokenCheck
  @ApiOperation(value = "根据作业的ID查询后续日志")
  @GetMapping(value = "/job/logs/next", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<TaskJobLogbackResponse> nextLogs(@RequestParam("id") Long id, @RequestParam("baseId") Long baseId) {
    return jobLogbackService.nextLog(id, baseId);
  }

}
