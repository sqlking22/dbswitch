package com.gitee.dbswitch.admin.model.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("JOB执行日志信息")
public class TaskJobLogbackResponse {

  @ApiModelProperty("最大的ID")
  private Long maxId = 0L;

  @ApiModelProperty("JOB状态")
  private Integer status = 0;

  @ApiModelProperty("日志列表")
  private List<String> logs = Collections.emptyList();
}
