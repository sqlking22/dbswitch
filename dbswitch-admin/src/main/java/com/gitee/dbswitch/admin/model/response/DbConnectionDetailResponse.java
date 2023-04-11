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
import com.gitee.dbswitch.admin.type.SupportDbTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.sql.Timestamp;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@ApiModel("连接详情")
public class DbConnectionDetailResponse {

  @ApiModelProperty("ID编号")
  private Long id;

  @ApiModelProperty("标题")
  private String name;

  @ApiModelProperty("数据库类型")
  private SupportDbTypeEnum type;

  @ApiModelProperty("驱动版本")
  private String version;

  @ApiModelProperty("驱动类")
  private String driver;

  @ApiModelProperty("URL连接串")
  private String url;

  @ApiModelProperty("账号名")
  private String username;

  @ApiModelProperty("密码")
  private String password;

  @ApiModelProperty("创建时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Timestamp createTime;

  @ApiModelProperty("更新时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
  private Timestamp updateTime;
}
