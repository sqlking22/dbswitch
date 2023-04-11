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
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("任务作业记录详情")
public class TaskJobDetailResponse {

  @ApiModelProperty("JOB的ID")
  private Long jobId;

  @ApiModelProperty("Task的ID")
  private Long assignmentId;

  @ApiModelProperty("调度模式")
  private String scheduleMode;

  @ApiModelProperty("开始时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date startTime;

  @ApiModelProperty("完成时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Date finishTime;

  @ApiModelProperty("持续时间(单位: 秒)")
  private Long duration;

  @ApiModelProperty("JOB状态")
  private Integer status;

  @ApiModelProperty("JOB状态描述")
  private String jobStatus;

  @ApiModelProperty("异常日志")
  private String errorLog;
}
