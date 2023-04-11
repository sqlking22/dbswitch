// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.common.exception;

import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@ControllerAdvice
public class ExceptionController {

  @ResponseBody
  @ExceptionHandler(value = Exception.class)
  public Result errorHandler(Exception ex) {
    if (ex instanceof DbswitchException) {
      DbswitchException learnException = (DbswitchException) ex;
      return Result.failed(learnException.getCode(), learnException.getMessage());
    }

    log.error("ERROR:", ex);
    if (ex instanceof NullPointerException) {
      return Result.failed(ResultCode.ERROR_INTERNAL_ERROR, "Null Pointer Exception");
    } else {
      return Result.failed(ResultCode.ERROR_INTERNAL_ERROR, ex.getMessage());
    }

  }
}
