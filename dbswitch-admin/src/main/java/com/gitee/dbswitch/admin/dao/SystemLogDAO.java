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

import com.gitee.dbswitch.admin.entity.SystemLogEntity;
import com.gitee.dbswitch.admin.mapper.SystemLogMapper;
import com.gitee.dbswitch.admin.type.LogTypeEnum;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Repository
public class SystemLogDAO {

  @Resource
  private SystemLogMapper systemLogMapper;

  public void insert(SystemLogEntity systemLogEntity) {
    systemLogMapper.insertSelective(systemLogEntity);
  }

  public List<SystemLogEntity> listAll(LogTypeEnum logType) {
    Example example = new Example(SystemLogEntity.class);
    Criteria criteria = example.createCriteria();

    SystemLogEntity condition = new SystemLogEntity();
    condition.setType(logType.getValue());

    criteria.andEqualTo(condition);
    example.orderBy("createTime").desc();
    return systemLogMapper.selectByExample(example);
  }

  public SystemLogEntity getById(Long id) {
    return systemLogMapper.selectByPrimaryKey(id);
  }

}
