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
import com.gitee.dbswitch.admin.common.exception.DbswitchException;
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.controller.converter.AssignmentDetailConverter;
import com.gitee.dbswitch.admin.controller.converter.AssignmentInfoConverter;
import com.gitee.dbswitch.admin.dao.AssignmentConfigDAO;
import com.gitee.dbswitch.admin.dao.AssignmentTaskDAO;
import com.gitee.dbswitch.admin.dao.DatabaseConnectionDAO;
import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.entity.DatabaseConnectionEntity;
import com.gitee.dbswitch.admin.model.request.AssigmentCreateRequest;
import com.gitee.dbswitch.admin.model.request.AssigmentUpdateRequest;
import com.gitee.dbswitch.admin.model.response.AssignmentDetailResponse;
import com.gitee.dbswitch.admin.model.response.AssignmentInfoResponse;
import com.gitee.dbswitch.admin.type.ScheduleModeEnum;
import com.gitee.dbswitch.admin.type.SupportDbTypeEnum;
import com.gitee.dbswitch.admin.util.PageUtils;
import com.gitee.dbswitch.data.config.DbswichProperties;
import com.gitee.dbswitch.data.entity.SourceDataSourceProperties;
import com.gitee.dbswitch.data.entity.TargetDataSourceProperties;
import com.gitee.dbswitch.data.util.JsonUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignmentService {

  @Resource
  private AssignmentTaskDAO assignmentTaskDAO;

  @Resource
  private AssignmentConfigDAO assignmentConfigDAO;

  @Resource
  private ScheduleService scheduleService;

  @Resource
  private DatabaseConnectionDAO databaseConnectionDAO;

  @Resource
  private DriverLoadService driverLoadService;

  @Transactional(rollbackFor = Exception.class)
  public AssignmentInfoResponse createAssignment(AssigmentCreateRequest request) {
    AssignmentTaskEntity assignment = request.toAssignmentTask();
    assignmentTaskDAO.insert(assignment);

    AssignmentConfigEntity assignmentConfigEntity = request.toAssignmentConfig(assignment.getId());
    assignmentConfigDAO.insert(assignmentConfigEntity);

    Long targetConnectionId = assignmentConfigEntity.getTargetConnectionId();
    DatabaseConnectionEntity entity = databaseConnectionDAO.getById(targetConnectionId);
    if (SupportDbTypeEnum.HIVE == entity.getType()) {
      throw new DbswitchException(ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG, "不支持目的端数据源为Hive");
    }
    if (SupportDbTypeEnum.SQLITE3 == entity.getType()) {
      if (SupportDbTypeEnum.isUnsupportedTargetSqlite(entity.getUrl())) {
        throw new DbswitchException(ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG,
            "不支持目的端数据源为远程服务器上的SQLite或内存方式下的SQLite");
      }
    }

    return ConverterFactory.getConverter(AssignmentInfoConverter.class)
        .convert(assignmentTaskDAO.getById(assignment.getId()));
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteAssignment(Long id) {
    assignmentTaskDAO.deleteById(id);
  }

  @Transactional(rollbackFor = Exception.class)
  public void updateAssignment(AssigmentUpdateRequest request) {
    AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(request.getId());
    if (Objects.isNull(assignmentTaskEntity)) {
      throw new DbswitchException(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "ID=" + request.getId());
    } else if (assignmentTaskEntity.getPublished()) {
      throw new DbswitchException(ResultCode.ERROR_RESOURCE_HAS_DEPLOY, "ID=" + request.getId());
    }

    AssignmentTaskEntity newAssignmentTaskEntity = request.toAssignmentTask();
    assignmentTaskDAO.updateById(newAssignmentTaskEntity);

    AssignmentConfigEntity assignmentConfigEntity = request
        .toAssignmentConfig(assignmentTaskEntity.getId());
    assignmentConfigDAO.deleteByAssignmentTaskId(assignmentTaskEntity.getId());
    assignmentConfigDAO.insert(assignmentConfigEntity);

    Long targetConnectionId = assignmentConfigEntity.getTargetConnectionId();
    DatabaseConnectionEntity entity = databaseConnectionDAO.getById(targetConnectionId);
    if (SupportDbTypeEnum.HIVE == entity.getType()) {
      throw new DbswitchException(ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG, "不支持目的端数据源为Hive");
    }
    if (SupportDbTypeEnum.SQLITE3 == entity.getType()) {
      if (SupportDbTypeEnum.isUnsupportedTargetSqlite(entity.getUrl())) {
        throw new DbswitchException(ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG,
            "不支持目的端数据源为远程服务器上的SQLite或内存方式下的SQLite");
      }
    }
  }

  public PageResult<AssignmentInfoResponse> listAll(String searchText, Integer page, Integer size) {
    Supplier<List<AssignmentInfoResponse>> method = () ->
        ConverterFactory.getConverter(AssignmentInfoConverter.class)
            .convert(assignmentTaskDAO.listAll(searchText));

    return PageUtils.getPage(method, page, size);
  }

  public Result<AssignmentDetailResponse> detailAssignment(Long id) {
    AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(id);
    if (Objects.isNull(assignmentTaskEntity)) {
      return Result.failed(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "ID=" + id);
    }

    AssignmentDetailResponse detailResponse = ConverterFactory
        .getConverter(AssignmentDetailConverter.class).convert(assignmentTaskEntity);
    return Result.success(detailResponse);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deployAssignments(List<Long> ids) {
    checkAssignmentAllExist(ids);
    ids.forEach(id -> {
      AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(id);
      if (assignmentTaskEntity.getPublished()) {
        throw new DbswitchException(ResultCode.ERROR_RESOURCE_HAS_DEPLOY, "ID=" + id);
      }
    });

    for (Long id : ids) {
      AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(id);
      AssignmentConfigEntity assignmentConfigEntity = assignmentConfigDAO.getByAssignmentTaskId(id);

      DbswichProperties properties = new DbswichProperties();
      SourceDataSourceProperties srcConfig = this.getSourceDataSourceProperties(
          assignmentConfigEntity);
      properties.setSource(Collections.singletonList(srcConfig));
      properties.setTarget(this.getTargetDataSourceProperties(assignmentConfigEntity));

      assignmentTaskEntity.setPublished(Boolean.TRUE);
      assignmentTaskEntity.setContent(JsonUtils.toJsonString(properties));
      assignmentTaskDAO.updateById(assignmentTaskEntity);

      ScheduleModeEnum systemScheduled = ScheduleModeEnum.SYSTEM_SCHEDULED;
      if (assignmentTaskEntity.getScheduleMode() == systemScheduled) {
        scheduleService.scheduleTask(assignmentTaskEntity.getId(), systemScheduled);
      }
    }

  }

  @Transactional(rollbackFor = Exception.class)
  public void runAssignments(List<Long> ids) {
    checkAssignmentAllExist(ids);
    List<AssignmentTaskEntity> tasks = new ArrayList<>();
    for (Long id : ids) {
      AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(id);
      if (assignmentTaskEntity.getPublished()) {
        tasks.add(assignmentTaskEntity);
      } else {
        throw new DbswitchException(ResultCode.ERROR_RESOURCE_NOT_DEPLOY, "ID=" + id);
      }
    }

    tasks.forEach(assignmentTask -> {
      scheduleService.scheduleTask(assignmentTask.getId(), ScheduleModeEnum.MANUAL);
    });

  }

  @Transactional(rollbackFor = Exception.class)
  public void retireAssignments(List<Long> ids) {
    checkAssignmentAllExist(ids);
    for (Long id : ids) {
      AssignmentTaskEntity assignmentTaskEntity = assignmentTaskDAO.getById(id);
      if (Objects.nonNull(assignmentTaskEntity.getPublished())
          && assignmentTaskEntity.getPublished()) {
        scheduleService.cancelByJobKey(assignmentTaskEntity.getJobKey());
        assignmentTaskEntity.setPublished(Boolean.FALSE);
        assignmentTaskEntity.setContent("{}");
        assignmentTaskEntity.setJobKey("");
        assignmentTaskDAO.updateById(assignmentTaskEntity);
      }
    }
  }

  private void checkAssignmentAllExist(List<Long> ids) {
    for (Long id : ids) {
      if (Objects.isNull(assignmentTaskDAO.getById(id))) {
        throw new DbswitchException(ResultCode.ERROR_RESOURCE_NOT_EXISTS, "ID=" + id);
      }
    }
  }

  private SourceDataSourceProperties getSourceDataSourceProperties(
      AssignmentConfigEntity assignmentConfigEntity) {
    SourceDataSourceProperties sourceDataSourceProperties = new SourceDataSourceProperties();
    DatabaseConnectionEntity sourceDatabaseConnectionEntity = databaseConnectionDAO.getById(
        assignmentConfigEntity.getSourceConnectionId()
    );
    File driverVersionFile = driverLoadService.getVersionDriverFile(sourceDatabaseConnectionEntity.getType(),
        sourceDatabaseConnectionEntity.getVersion());
    sourceDataSourceProperties.setUrl(sourceDatabaseConnectionEntity.getUrl());
    sourceDataSourceProperties.setDriverClassName(sourceDatabaseConnectionEntity.getDriver());
    sourceDataSourceProperties.setDriverPath(driverVersionFile.getAbsolutePath());
    sourceDataSourceProperties.setUsername(sourceDatabaseConnectionEntity.getUsername());
    sourceDataSourceProperties.setPassword(sourceDatabaseConnectionEntity.getPassword());

    String sourceSchema = assignmentConfigEntity.getSourceSchema();
    if (assignmentConfigEntity.getExcluded()) {
      if (CollectionUtils.isEmpty(assignmentConfigEntity.getSourceTables())) {
        sourceDataSourceProperties.setSourceExcludes("");
      } else {
        sourceDataSourceProperties.setSourceExcludes(
            assignmentConfigEntity.getSourceTables()
                .stream().collect(Collectors.joining(","))
        );
      }
    } else {
      if (CollectionUtils.isEmpty(assignmentConfigEntity.getSourceTables())) {
        sourceDataSourceProperties.setSourceIncludes("");
      } else {
        sourceDataSourceProperties.setSourceIncludes(
            assignmentConfigEntity.getSourceTables()
                .stream().collect(Collectors.joining(","))
        );
      }
    }
    sourceDataSourceProperties.setSourceSchema(sourceSchema);
    sourceDataSourceProperties.setRegexTableMapper(assignmentConfigEntity.getTableNameMap());
    sourceDataSourceProperties.setRegexColumnMapper(assignmentConfigEntity.getColumnNameMap());
    sourceDataSourceProperties.setFetchSize(assignmentConfigEntity.getBatchSize());
    return sourceDataSourceProperties;
  }

  private TargetDataSourceProperties getTargetDataSourceProperties(
      AssignmentConfigEntity assignmentConfigEntity) {
    TargetDataSourceProperties targetDataSourceProperties = new TargetDataSourceProperties();
    DatabaseConnectionEntity targetDatabaseConnectionEntity = databaseConnectionDAO
        .getById(assignmentConfigEntity.getTargetConnectionId());
    File driverVersionFile = driverLoadService.getVersionDriverFile(targetDatabaseConnectionEntity.getType(),
        targetDatabaseConnectionEntity.getVersion());
    targetDataSourceProperties.setUrl(targetDatabaseConnectionEntity.getUrl());
    targetDataSourceProperties.setDriverClassName(targetDatabaseConnectionEntity.getDriver());
    targetDataSourceProperties.setDriverPath(driverVersionFile.getAbsolutePath());
    targetDataSourceProperties.setUsername(targetDatabaseConnectionEntity.getUsername());
    targetDataSourceProperties.setPassword(targetDatabaseConnectionEntity.getPassword());
    targetDataSourceProperties.setTargetSchema(assignmentConfigEntity.getTargetSchema());
    if (assignmentConfigEntity.getTargetDropTable()) {
      targetDataSourceProperties.setTargetDrop(Boolean.TRUE);
      targetDataSourceProperties.setChangeDataSync(Boolean.FALSE);
    } else {
      targetDataSourceProperties.setTargetDrop(Boolean.FALSE);
      targetDataSourceProperties.setChangeDataSync(Boolean.TRUE);
    }
    if (assignmentConfigEntity.getTargetOnlyCreate()) {
      targetDataSourceProperties.setOnlyCreate(Boolean.TRUE);
    }

    return targetDataSourceProperties;
  }

}
