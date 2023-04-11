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

import com.gitee.dbswitch.admin.entity.JobLogbackEntity;
import com.gitee.dbswitch.admin.mapper.JobLogbackMapper;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

@Repository
public class JobLogbackDAO {

  @Resource
  private JobLogbackMapper jobLogbackMapper;

  public void insert(String uuid, String content) {
    jobLogbackMapper.insertSelective(JobLogbackEntity.builder().uuid(uuid).content(content).build());
  }

  public List<JobLogbackEntity> getTailByUuid(String uuid) {
    Example example = Example.builder(JobLogbackEntity.class)
        .select("id", "content")
        .andWhere(Sqls.custom().andEqualTo("uuid", uuid))
        .orderByDesc("id")
        .build();
    List<JobLogbackEntity> result = jobLogbackMapper.selectByExample(example);
    Collections.reverse(result);
    return result;
  }

  public List<JobLogbackEntity> getNextByUuid(String uuid, Long baseId) {
    Example example = Example.builder(JobLogbackEntity.class)
        .select("id", "content")
        .andWhere(Sqls.custom().andGreaterThan("id", baseId).andEqualTo("uuid", uuid))
        .build();
    return jobLogbackMapper.selectByExample(example);
  }

  public void deleteOldest(Integer days) {
    if (Objects.nonNull(days)) {
      jobLogbackMapper.deleteByDays(days);
    }
  }

}
