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
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.config.SwaggerConfig;
import com.gitee.dbswitch.admin.model.request.AssigmentCreateRequest;
import com.gitee.dbswitch.admin.model.request.AssigmentUpdateRequest;
import com.gitee.dbswitch.admin.model.response.AssignmentDetailResponse;
import com.gitee.dbswitch.admin.model.response.AssignmentInfoResponse;
import com.gitee.dbswitch.admin.service.AssignmentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"任务管理接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/assignment")
public class AssignmentController {

  @Resource
  private AssignmentService assignmentService;

  @TokenCheck
  @LogOperate(name = "创建任务", description = "'创建任务的名称为：'+#request.name")
  @ApiOperation(value = "创建")
  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<AssignmentInfoResponse> createAssignment(
      @RequestBody AssigmentCreateRequest request) {
    return Result.success(assignmentService.createAssignment(request));
  }

  @TokenCheck
  @LogOperate(name = "修改任务", description = "'修改任务的名称为：'+#request.name")
  @ApiOperation(value = "修改")
  @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result updateAssignment(@RequestBody AssigmentUpdateRequest request) {
    assignmentService.updateAssignment(request);
    return Result.success();
  }

  @TokenCheck
  @LogOperate(name = "删除任务", description = "'删除任务的ID为：'+#id")
  @ApiOperation(value = "删除")
  @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result deleteAssignment(@PathVariable("id") Long id) {
    assignmentService.deleteAssignment(id);
    return Result.success();
  }

  @TokenCheck
  @ApiOperation(value = "任务列表")
  @GetMapping(value = "/list/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResult<AssignmentInfoResponse> listAssignment(
      @PathVariable("page") Integer page,
      @PathVariable("size") Integer size) {
    return assignmentService.listAll(null, page, size);
  }

  @TokenCheck
  @ApiOperation(value = "任务详情")
  @GetMapping(value = "/detail/id/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<AssignmentDetailResponse> detailAssignment(
      @PathVariable("id") Long id) {
    return assignmentService.detailAssignment(id);
  }

  @TokenCheck
  @LogOperate(name = "发布任务", description = "'发布任务的ID为：'+#ids")
  @ApiOperation(value = "发布")
  @PostMapping(value = "/deploy", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result deployAssignments(
      @RequestParam(value = "ids") List<Long> ids) {
    assignmentService.deployAssignments(ids);
    return Result.success();
  }

  @TokenCheck
  @LogOperate(name = "手动执行任务", description = "'手动执行任务的ID为：'+#ids")
  @ApiOperation(value = "手动执行")
  @PostMapping(value = "/run", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result runAssignments(@RequestBody List<Long> ids) {
    assignmentService.runAssignments(ids);
    return Result.success();
  }

  @TokenCheck
  @LogOperate(name = "下线任务", description = "'下线任务的ID为：'+#ids")
  @ApiOperation(value = "下线")
  @PostMapping(value = "/retire", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result retireAssignments(@RequestParam(value = "ids") List<Long> ids) {
    assignmentService.retireAssignments(ids);
    return Result.success();
  }

}
