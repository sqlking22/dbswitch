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
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("系统日志")
public class SystemLogDetailResponse {

  @ApiModelProperty("ID编号")
  private Long id;

  @ApiModelProperty("操作用户")
  private String username;

  @ApiModelProperty("客户端地址")
  private String ipAddress;

  @ApiModelProperty("模块名称")
  private String moduleName;

  @ApiModelProperty("日志详情")
  private String content;

  @ApiModelProperty("请求路径")
  private String urlPath;

  @ApiModelProperty("浏览器代理")
  private String userAgent;

  @ApiModelProperty("是否异常")
  private Boolean failed;

  @ApiModelProperty("异常信息")
  private String exception;

  @ApiModelProperty("接口耗时")
  private Long elapseSeconds;

  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Timestamp createTime;
}
