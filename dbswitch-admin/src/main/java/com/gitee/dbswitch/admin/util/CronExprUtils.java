// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.util;

import com.gitee.dbswitch.admin.common.exception.DbswitchException;
import com.gitee.dbswitch.admin.common.response.ResultCode;
import java.text.ParseException;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.quartz.CronExpression;

/**
 * CRON表达式工具类
 */
public final class CronExprUtils {

  public static final int MIN_INTERVAL_SECONDS = 120;

  /**
   * 检查CRON表达式的有效性
   *
   * @param cronExpression     CRON表达式
   * @param minIntervalSeconds 最小间隔时间（单位：秒）
   */
  public static void checkCronExpressionValid(String cronExpression, int minIntervalSeconds) {
    if (StringUtils.isNotBlank(cronExpression)) {
      CronExpression expression;
      try {
        expression = new CronExpression(cronExpression);
      } catch (ParseException e) {
        throw new DbswitchException(ResultCode.ERROR_INVALID_ARGUMENT, String.format("cron表达式%s无效"));
      }
      Date nextDate = expression.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
      if (null == nextDate) {
        throw new DbswitchException(ResultCode.ERROR_INVALID_ARGUMENT,
            String.format("cron表达式[%s]不可以在历史时间运行", cronExpression));
      }
      Date calculateDate = expression.getNextValidTimeAfter(new Date(nextDate.getTime() + 1));
      if (null != calculateDate) {
        long intervalSeconds = (calculateDate.getTime() - nextDate.getTime()) / 1000;
        if (intervalSeconds < minIntervalSeconds) {
          throw new DbswitchException(ResultCode.ERROR_INVALID_ARGUMENT,
              String.format("cron表达式[%s]运行间隔时间为%d秒, 小于设定的阈值 [%s秒]",
                  cronExpression, intervalSeconds, minIntervalSeconds));
        }
      }
    }
  }
}
