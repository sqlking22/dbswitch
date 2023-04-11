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

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatusEnum {

  INIT(0, "未执行"),
  RUNNING(1, "运行中"),
  FAIL(2, "失败"),
  PASS(3, "成功"),
  CANCEL(4, "手动终止"),
  ;

  private int value;
  private String name;

  public static JobStatusEnum of(String name) {
    for (JobStatusEnum status : JobStatusEnum.values()) {
      if (status.name().equalsIgnoreCase(name)) {
        return status;
      }
    }

    throw new IllegalArgumentException("cannot find enum name: " + name);
  }

  public static JobStatusEnum of(int value) {
    for (JobStatusEnum status : JobStatusEnum.values()) {
      if (status.value == value) {
        return status;
      }
    }

    throw new IllegalArgumentException("cannot find enum value: " + value);
  }

}
