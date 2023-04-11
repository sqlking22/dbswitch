// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.model.request;

import com.gitee.dbswitch.admin.common.exception.DbswitchException;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.type.IncludeExcludeEnum;
import com.gitee.dbswitch.admin.type.ScheduleModeEnum;
import com.gitee.dbswitch.admin.util.CronExprUtils;
import com.gitee.dbswitch.common.entity.PatternMapper;
import com.gitee.dbswitch.common.type.DBTableType;
import com.gitee.dbswitch.common.util.PatterNameUtils;
import java.util.List;
import java.util.Objects;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@NoArgsConstructor
@Data
public class AssigmentCreateRequest {

  private String name;
  private String description;
  private ScheduleModeEnum scheduleMode;
  private String cronExpression;
  private AssigmentCreateConfig config;

  @NoArgsConstructor
  @Data
  public static class AssigmentCreateConfig {

    private Long sourceConnectionId;
    private String sourceSchema;
    private DBTableType tableType;
    private IncludeExcludeEnum includeOrExclude;
    private List<String> sourceTables;
    private Long targetConnectionId;
    private String targetSchema;
    private List<PatternMapper> tableNameMapper;
    private List<PatternMapper> columnNameMapper;
    private Boolean targetDropTable;
    private Boolean targetOnlyCreate;
    private Integer batchSize;
  }

  public AssignmentTaskEntity toAssignmentTask() {
    AssignmentTaskEntity assignment = new AssignmentTaskEntity();
    assignment.setId(null);
    assignment.setName(name);
    assignment.setDescription(description);
    assignment.setScheduleMode(scheduleMode);
    if (ScheduleModeEnum.SYSTEM_SCHEDULED == this.getScheduleMode()) {
      CronExprUtils.checkCronExpressionValid(this.getCronExpression(), CronExprUtils.MIN_INTERVAL_SECONDS);
      assignment.setCronExpression(this.getCronExpression());
    }

    return assignment;
  }

  public AssignmentConfigEntity toAssignmentConfig(Long assignmentId) {
    if (Objects.equals(config.getSourceConnectionId(), config.getTargetConnectionId())) {
      throw new DbswitchException(ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG, "源端与目标端不能相同");
    }

    AssignmentConfigEntity assignmentConfigEntity = new AssignmentConfigEntity();
    assignmentConfigEntity.setAssignmentId(assignmentId);
    assignmentConfigEntity.setSourceConnectionId(this.getConfig().getSourceConnectionId());
    assignmentConfigEntity.setSourceSchema(this.getConfig().getSourceSchema());
    assignmentConfigEntity.setTableType(this.getConfig().getTableType());
    assignmentConfigEntity.setSourceTables(this.getConfig().getSourceTables());
    assignmentConfigEntity.setExcluded(
        this.getConfig().getIncludeOrExclude() == IncludeExcludeEnum.EXCLUDE
    );
    assignmentConfigEntity.setTargetConnectionId(this.getConfig().getTargetConnectionId());
    assignmentConfigEntity.setTargetSchema(this.getConfig().getTargetSchema());
    assignmentConfigEntity.setTableNameMap(this.getConfig().getTableNameMapper());
    assignmentConfigEntity.setColumnNameMap(this.getConfig().getColumnNameMapper());
    assignmentConfigEntity.setTargetDropTable(this.getConfig().getTargetDropTable());
    assignmentConfigEntity.setTargetOnlyCreate(this.getConfig().getTargetOnlyCreate());
    assignmentConfigEntity.setBatchSize(
        Objects.isNull(this.config.getBatchSize())
            ? 10000
            : this.config.getBatchSize()
    );
    assignmentConfigEntity.setFirstFlag(Boolean.TRUE);

    if (!assignmentConfigEntity.getExcluded()
        && !CollectionUtils.isEmpty(assignmentConfigEntity.getSourceTables())) {
      for (String tableName : assignmentConfigEntity.getSourceTables()) {
        String targetTableName = PatterNameUtils.getFinalName(tableName,
            assignmentConfigEntity.getTableNameMap());
        if (StringUtils.isEmpty(targetTableName)) {
          throw new DbswitchException(
              ResultCode.ERROR_INVALID_ASSIGNMENT_CONFIG,
              "表名的映射关系配置有误，不允许将表[" + tableName + "]映射为空");
        }
      }
    }

    return assignmentConfigEntity;
  }

}
