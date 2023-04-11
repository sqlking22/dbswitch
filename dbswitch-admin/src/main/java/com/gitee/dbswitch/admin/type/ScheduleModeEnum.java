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
public enum ScheduleModeEnum {

  MANUAL(1, "手动执行"),
  SYSTEM_SCHEDULED(2, "系统调度"),
  ;

  private final Integer value;
  private final String name;

  public static ScheduleModeEnum of(int value) {
    for (ScheduleModeEnum mode : ScheduleModeEnum.values()) {
      if (mode.value == value) {
        return mode;
      }
    }

    throw new IllegalArgumentException("cannot find such value: " + value);
  }

}
