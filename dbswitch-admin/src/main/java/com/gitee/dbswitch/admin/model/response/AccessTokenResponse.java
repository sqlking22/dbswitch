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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("TOKEN信息")
public class AccessTokenResponse {

  @ApiModelProperty("实际名称")
  private String realName;

  @ApiModelProperty("token字符串")
  private String accessToken;

  @ApiModelProperty("有效时间(单位秒)")
  private Long expireSeconds;
}
