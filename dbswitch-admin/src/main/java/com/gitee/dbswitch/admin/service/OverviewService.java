// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.service;

import com.gitee.dbswitch.admin.dao.AssignmentJobDAO;
import com.gitee.dbswitch.admin.dao.AssignmentTaskDAO;
import com.gitee.dbswitch.admin.dao.DatabaseConnectionDAO;
import com.gitee.dbswitch.admin.model.ops.OpsTaskJobTrend;
import com.gitee.dbswitch.admin.model.response.OverviewStatisticsResponse;
import com.gitee.dbswitch.admin.model.response.OverviewStatisticsResponse.AssignmentJobStatistics;
import com.gitee.dbswitch.admin.model.response.OverviewStatisticsResponse.AssignmentTaskStatistics;
import com.gitee.dbswitch.admin.model.response.OverviewStatisticsResponse.ConnectionStatistics;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class OverviewService {

  @Resource
  private DatabaseConnectionDAO databaseConnectionDAO;

  @Resource
  private AssignmentTaskDAO assignmentTaskDAO;

  @Resource
  private AssignmentJobDAO assignmentJobDAO;

  public OverviewStatisticsResponse statistics() {
    OverviewStatisticsResponse response = new OverviewStatisticsResponse();

    ConnectionStatistics connectionStatistics = new ConnectionStatistics();
    connectionStatistics.setTotalCount(databaseConnectionDAO.getTotalCount());

    response.setConnectionStatistics(connectionStatistics);

    AssignmentTaskStatistics assignmentTaskStatistics = new AssignmentTaskStatistics();
    assignmentTaskStatistics.setTotalCount(assignmentTaskDAO.getTotalCount());
    assignmentTaskStatistics.setPublishedCount(assignmentTaskDAO.getPublishedCount());

    response.setAssignmentTaskStatistics(assignmentTaskStatistics);

    AssignmentJobStatistics assignmentJobStatistics = new AssignmentJobStatistics();
    assignmentJobStatistics.setTotalCount(assignmentJobDAO.getTotalCount());
    assignmentJobStatistics
        .setRunningCount(assignmentJobDAO.getCountByStatus(JobStatusEnum.RUNNING));
    assignmentJobStatistics
        .setSuccessfulCount(assignmentJobDAO.getCountByStatus(JobStatusEnum.PASS));
    assignmentJobStatistics.setFailedCount(assignmentJobDAO.getCountByStatus(JobStatusEnum.FAIL));

    response.setAssignmentJobStatistics(assignmentJobStatistics);

    return response;
  }

  public List<OpsTaskJobTrend> trend(Integer days) {
    return assignmentJobDAO.queryTaskJobTrend(days);
  }

}
