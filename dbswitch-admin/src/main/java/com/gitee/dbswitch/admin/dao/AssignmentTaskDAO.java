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

import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.mapper.AssignmentTaskMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Repository
public class AssignmentTaskDAO {

  @Resource
  private AssignmentTaskMapper assignmentTaskMapper;

  public void insert(AssignmentTaskEntity assignment) {
    assignmentTaskMapper.insertSelective(assignment);
  }

  public void updateById(AssignmentTaskEntity assignment) {
    assignmentTaskMapper.updateByPrimaryKeySelective(assignment);
  }

  public List<AssignmentTaskEntity> listAll(String searchText) {
    Example example = new Example(AssignmentTaskEntity.class);
    if (!StringUtils.isEmpty(searchText)) {
      Criteria criteria = example.createCriteria();
      criteria.andLike("name", "%" + searchText + "%");
    }
    example.orderBy("createTime").desc();
    return assignmentTaskMapper.selectByExample(example);
  }

  public AssignmentTaskEntity getById(Long id) {
    return assignmentTaskMapper.selectByPrimaryKey(id);
  }

  public void deleteById(Long id) {
    assignmentTaskMapper.deleteByPrimaryKey(id);
  }

  public int getTotalCount() {
    return Optional.ofNullable(assignmentTaskMapper.selectAll())
        .orElseGet(ArrayList::new).size();
  }

  public int getPublishedCount() {
    AssignmentTaskEntity condition = new AssignmentTaskEntity();
    condition.setPublished(Boolean.TRUE);

    Example example = new Example(AssignmentTaskEntity.class);
    example.createCriteria().andEqualTo(condition);
    return assignmentTaskMapper.selectCountByExample(example);
  }

}
