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

import java.io.IOException;
import java.util.Properties;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

/**
 * 同时支持.properties和.yaml两种配置类型
 *
 * @author tang
 */
public class DbswitchPropertySourceFactory extends DefaultPropertySourceFactory {

  private static final String suffixYml = ".yml";
  private static final String suffixYaml = ".yaml";

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource resource)
      throws IOException {
    String sourceName = name != null ? name : resource.getResource().getFilename();
    if (!resource.getResource().exists()) {
      return new PropertiesPropertySource(sourceName, new Properties());
    } else if (sourceName.endsWith(suffixYml) || sourceName.endsWith(suffixYaml)) {
      YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
      factory.setResources(resource.getResource());
      factory.afterPropertiesSet();
      return new PropertiesPropertySource(sourceName, factory.getObject());
    } else {
      return super.createPropertySource(name, resource);
    }
  }

}
