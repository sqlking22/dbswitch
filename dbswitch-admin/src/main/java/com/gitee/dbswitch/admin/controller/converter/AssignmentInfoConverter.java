// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.controller.converter;

import com.gitee.dbswitch.admin.common.converter.AbstractConverter;
import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.model.response.AssignmentInfoResponse;

public class AssignmentInfoConverter extends
    AbstractConverter<AssignmentTaskEntity, AssignmentInfoResponse> {

  @Override
  public AssignmentInfoResponse convert(AssignmentTaskEntity assignmentTaskEntity) {
    AssignmentInfoResponse response = new AssignmentInfoResponse();
    response.setId(assignmentTaskEntity.getId());
    response.setName(assignmentTaskEntity.getName());
    response.setDescription(assignmentTaskEntity.getDescription());
    response.setScheduleMode(assignmentTaskEntity.getScheduleMode());
    response.setCronExpression(assignmentTaskEntity.getCronExpression());
    response.setIsPublished(assignmentTaskEntity.getPublished());
    response.setCreateTime(assignmentTaskEntity.getCreateTime());
    response.setUpdateTime(assignmentTaskEntity.getUpdateTime());

    return response;
  }
}
