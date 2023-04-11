// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.type;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogTypeEnum {

  ACCESS_LOG(1, "访问日志"),
  OPERRATE_LOG(2, "操作日志"),
  ;

  private int value;
  private String description;

  public static LogTypeEnum of(Integer v) {
    if (!Objects.isNull(v)) {
      for (LogTypeEnum type : LogTypeEnum.values()) {
        if (v.intValue() == type.getValue()) {
          return type;
        }
      }
    }

    throw new IllegalArgumentException("no such value: " + v);
  }

}
