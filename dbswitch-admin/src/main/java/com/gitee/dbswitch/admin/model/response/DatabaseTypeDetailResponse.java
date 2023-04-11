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
@ApiModel("数据库类型")
public class DatabaseTypeDetailResponse {

  @ApiModelProperty("编号")
  private Integer id;

  @ApiModelProperty("数据库类型")
  private String type;

  @ApiModelProperty("驱动类")
  private String driver;

  @ApiModelProperty("连接串模板")
  private String template;
}
