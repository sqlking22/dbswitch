// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.dao;

import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.mapper.AssignmentConfigMapper;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

@Repository
public class AssignmentConfigDAO {

  @Resource
  private AssignmentConfigMapper assignmentConfigMapper;

  public void insert(AssignmentConfigEntity assignmentConfigEntity) {
    assignmentConfigMapper.insertSelective(assignmentConfigEntity);
  }

  public AssignmentConfigEntity getById(Long id) {
    return assignmentConfigMapper.selectByPrimaryKey(id);
  }

  public AssignmentConfigEntity getByAssignmentTaskId(Long assignmentId) {
    AssignmentConfigEntity record = new AssignmentConfigEntity();
    record.setAssignmentId(assignmentId);
    return assignmentConfigMapper.selectOne(record);
  }

  public void updateSelective(AssignmentConfigEntity assignmentConfigEntity) {
    assignmentConfigMapper.updateByPrimaryKeySelective(assignmentConfigEntity);
  }

  public void deleteByAssignmentTaskId(Long taskId) {
    Example example = Example.builder(AssignmentConfigEntity.class)
        .andWhere(Sqls.custom().andEqualTo("assignmentId", taskId))
        .build();
    assignmentConfigMapper.deleteByExample(example);
  }

}
