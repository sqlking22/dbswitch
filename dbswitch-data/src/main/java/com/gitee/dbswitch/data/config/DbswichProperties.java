// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.data.config;

import com.gitee.dbswitch.data.entity.SourceDataSourceProperties;
import com.gitee.dbswitch.data.entity.TargetDataSourceProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * 属性映射配置
 *
 * @author tang
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "dbswitch", ignoreUnknownFields = false)
@PropertySource(
    value = {"classpath:config.properties", "classpath:config.yml"},
    ignoreResourceNotFound = true,
    factory = DbswitchPropertySourceFactory.class)
public class DbswichProperties {

  private List<SourceDataSourceProperties> source = new ArrayList<>();

  private TargetDataSourceProperties target = new TargetDataSourceProperties();
}
