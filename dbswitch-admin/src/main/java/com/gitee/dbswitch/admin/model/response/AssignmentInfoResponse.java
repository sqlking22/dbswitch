// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gitee.dbswitch.admin.type.ScheduleModeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("任务配置信息")
public class AssignmentInfoResponse {

  @ApiModelProperty("ID编号")
  private Long id;

  @ApiModelProperty("任务名")
  private String name;

  @ApiModelProperty("描述")
  private String description;

  @ApiModelProperty("调度模式")
  private ScheduleModeEnum scheduleMode;

  @ApiModelProperty("Cron表达式")
  private String cronExpression;

  @ApiModelProperty("是否已发布")
  private Boolean isPublished;

  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Timestamp createTime;

  @ApiModelProperty("更新时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Timestamp updateTime;
}
