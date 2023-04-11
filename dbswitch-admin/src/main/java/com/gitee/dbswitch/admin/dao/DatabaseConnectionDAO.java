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

import com.gitee.dbswitch.admin.entity.DatabaseConnectionEntity;
import com.gitee.dbswitch.admin.mapper.DatabaseConnectionMapper;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

@Repository
public class DatabaseConnectionDAO {

  @Resource
  private DatabaseConnectionMapper databaseConnectionMapper;

  public void insert(DatabaseConnectionEntity databaseConnectionEntity) {
    databaseConnectionMapper.insertSelective(databaseConnectionEntity);
  }

  public DatabaseConnectionEntity getById(Long id) {
    return databaseConnectionMapper.selectByPrimaryKey(id);
  }

  public DatabaseConnectionEntity getByName(String name) {
    DatabaseConnectionEntity record = new DatabaseConnectionEntity();
    record.setName(name);
    return databaseConnectionMapper.selectOne(record);
  }

  public List<DatabaseConnectionEntity> listAll(String searchText) {
    Example example = new Example(DatabaseConnectionEntity.class);
    if (!StringUtils.isEmpty(searchText)) {
      Criteria criteria = example.createCriteria();
      criteria.andLike("name", "%" + searchText + "%");
    }
    example.orderBy("createTime").desc();
    return databaseConnectionMapper.selectByExample(example);
  }

  public void updateById(DatabaseConnectionEntity databaseConnectionEntity) {
    databaseConnectionMapper.updateByPrimaryKeySelective(databaseConnectionEntity);
  }

  public void deleteById(Long id) {
    databaseConnectionMapper.deleteByPrimaryKey(id);
  }

  public int getTotalCount() {
    return databaseConnectionMapper.selectCount(null);
  }

}
