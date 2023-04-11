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

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("概述统计")
public class OverviewStatisticsResponse {

  @ApiModelProperty("连接")
  private ConnectionStatistics connectionStatistics;

  @ApiModelProperty("任务")
  private AssignmentTaskStatistics assignmentTaskStatistics;

  @ApiModelProperty("执行")
  private AssignmentJobStatistics assignmentJobStatistics;

  @NoArgsConstructor
  @Data
  @ApiModel("连接")
  public static class ConnectionStatistics {

    @ApiModelProperty("连接总数")
    private Integer totalCount;
  }

  @NoArgsConstructor
  @Data
  @ApiModel("任务")
  public static class AssignmentTaskStatistics {

    @ApiModelProperty("任务总数")
    private Integer totalCount;

    @ApiModelProperty("发布总数")
    private Integer publishedCount;
  }

  @NoArgsConstructor
  @Data
  @ApiModel("作业")
  public static class AssignmentJobStatistics {

    @ApiModelProperty("Job总数")
    private Integer totalCount;

    @ApiModelProperty("执行中的Job总数")
    private Integer runningCount;

    @ApiModelProperty("已经成功的总数")
    private Integer successfulCount;

    @ApiModelProperty("已经失败的总数")
    private Integer failedCount;
  }

}
