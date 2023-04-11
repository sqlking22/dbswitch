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

import com.gitee.dbswitch.admin.entity.SystemUserEntity;
import com.gitee.dbswitch.admin.mapper.SystemUserMapper;
import java.util.Objects;
import javax.annotation.Resource;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.Sqls;

@Repository
public class SystemUserDAO {

  @Resource
  private SystemUserMapper systemUserMapper;

  public SystemUserEntity getById(Long id) {
    return systemUserMapper.selectByPrimaryKey(id);
  }

  public SystemUserEntity findByUsername(String username) {
    return systemUserMapper.selectOneByExample(
        Example.builder(SystemUserEntity.class)
            .where(
                Sqls.custom()
                    .andEqualTo("username", username)
            )
            .build()
    );
  }

  public void updateUserPassword(String username, String newPassword) {
    SystemUserEntity userEntity = findByUsername(username);
    if (Objects.nonNull(userEntity)) {
      userEntity.setPassword(newPassword);
      systemUserMapper.updateByPrimaryKeySelective(userEntity);
    }
  }

}
