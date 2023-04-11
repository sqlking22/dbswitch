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

import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.model.ops.OpsTaskJobTrend;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface AssignmentJobMapper extends Mapper<AssignmentJobEntity> {

  @Select("<script>"
      + "<if test='_databaseId == \"mysql\" '>"
      + " SELECT \n"
      + " \t DATE_FORMAT(create_time,'%Y-%m-%d') as of_date , \n"
      + " \t count(*) as count_of_job,\n"
      + " \t count(DISTINCT assignment_id) as count_of_task \n"
      + "  FROM \n"
      + " ( \n"
      + " \t SELECT * FROM DBSWITCH_ASSIGNMENT_JOB\n"
      + " \t WHERE DATE_SUB( CURDATE(), INTERVAL ${days} DAY ) &lt;= date(create_time)\n"
      + " ) t \n"
      + " GROUP BY of_date "
      + "</if>"
      + "<if test='_databaseId == \"postgresql\" '>"
      + "  SELECT \n"
      + " \t to_char(create_time, 'YYYY-MM-DD') as of_date , \n"
      + " \t count(*) as count_of_job,\n"
      + " \t count(DISTINCT assignment_id) as count_of_task \n"
      + "  FROM \n"
      + " ( \n"
      + " \t SELECT * FROM DBSWITCH_ASSIGNMENT_JOB\n"
      + " \t WHERE CURRENT_DATE - INTERVAL'${days} day' &lt;= create_time::date\n"
      + " ) t \n"
      + " GROUP BY of_date; "
      + "</if>"
      + "</script>")
  @Results({
      @Result(column = "of_date", property = "dateOfDay"),
      @Result(column = "count_of_job", property = "countOfJob"),
      @Result(column = "count_of_task", property = "countOfTask")
  })
  List<OpsTaskJobTrend> queryTaskJobTrend(@Param("days") Integer days);

}
