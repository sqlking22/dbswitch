// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.common.util;

import java.util.UUID;

/**
 * UUID工具类
 */
public final class UuidUtils {

  public static String generateUuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

}
