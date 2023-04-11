// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.mapper;

import com.gitee.dbswitch.admin.entity.JobLogbackEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface JobLogbackMapper extends Mapper<JobLogbackEntity> {

  @Delete("<script>"
      + "<if test='_databaseId == \"mysql\" '>"
      + "DELETE FROM DBSWITCH_JOB_LOGBACK WHERE date(create_time) &lt; DATE_SUB( CURDATE(), INTERVAL ${days} DAY )"
      + "</if>"
      + "<if test='_databaseId == \"postgresql\" '>"
      + "DELETE FROM DBSWITCH_JOB_LOGBACK WHERE create_time::date &lt; CURRENT_DATE - INTERVAL'${days} day'"
      + "</if>"
      + "</script>")
  void deleteByDays(@Param("days") Integer days);
}
