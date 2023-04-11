// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.config;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Slf4j
@Configuration("dbswitchQuartzConfig")
public class QuartzConfig {

  @Bean("quartzProperties")
  public Properties quartzProperties(DataSourceProperties dataSourceProperties) throws IOException {
    PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();

    Properties prop = new Properties();

    /////////////////////////////////////
    // 基础配置
    /////////////////////////////////////

    //调度标识名 集群中每一个实例都必须使用相同的名称,如果使用JobStoreTX，实例名严禁使用：DefaultQuartzScheduler
    prop.put("org.quartz.scheduler.instanceName", "DBSwitch-Quartz-Scheduler");
    //调度器实例编号自动生成，每个实例不能不能相同,如果使用集群，instanceId必须唯一，设置成AUTO
    prop.put("org.quartz.scheduler.instanceId", "AUTO");
    prop.put("org.quartz.scheduler.rmi.export", "false");
    prop.put("org.quartz.scheduler.rmi.proxy", "false");
    prop.put("org.quartz.scheduler.wrapJobExecutionInUserTransaction", "false");

    /////////////////////////////////////
    // 调度器线程池配置
    /////////////////////////////////////

    //实例化ThreadPool时，使用的线程类为SimpleThreadPool（一般使用SimpleThreadPool即可满足几乎所有用户的需求）
    prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
    //并发个数,指定线程数，至少为1（无默认值）(一般设置为1-100之间的的整数合适)
    prop.put("org.quartz.threadPool.threadCount", "20");
    //设置线程的优先级（最大为java.lang.Thread.MAX_PRIORITY 10，最小为Thread.MIN_PRIORITY 1，默认为5）
    prop.put("org.quartz.threadPool.threadPriority", "5");
    //加载任务代码的ClassLoader是否从外部继承
    prop.put("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

    /////////////////////////////////////
    // Configure JobStore 作业存储配置
    /////////////////////////////////////

    // 数据库方式 JobStore配置
    prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
    //持久化方式配置数据驱动
    prop.put("org.quartz.jobStore.driverDelegateClass",
        "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
    prop.put("org.quartz.jobStore.useProperties", "true");
    //开启分布式部署，集群
    prop.put("org.quartz.jobStore.isClustered", "true");
    //容许的最大作业延长时间,最大能忍受的触发超时时间，如果超过则认为"失误",不敢再内存中还是数据中都要配置
    prop.put("org.quartz.jobStore.misfireThreshold", "12000");
    //quartz相关数据表前缀名
    prop.put("org.quartz.jobStore.tablePrefix", "DBSWITCH_");

    // 如果使用的PostgreSQL作为配置数据库，则需要补充如下配置：
    // https://blog.csdn.net/wsdhla/article/details/122460119
    if (StringUtils.isNotBlank(dataSourceProperties.getUrl())
        && dataSourceProperties.getUrl().startsWith("jdbc:postgresql://")) {
      prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.PostgreSQLDelegate");
    }

    propertiesFactoryBean.setProperties(prop);
    propertiesFactoryBean.afterPropertiesSet();

    return propertiesFactoryBean.getObject();
  }

  /**
   * SchedulerFactoryBean提供了对org.quartz.Scheduler的创建与配置，并且会管理它的生命周期与Spring同步
   */
  @Bean
  public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory,
      Properties quartzProperties) {
    SchedulerFactoryBean factory = new SchedulerFactoryBean();
    factory.setOverwriteExistingJobs(true);
    factory.setAutoStartup(true);
    factory.setDataSource(dataSource);
    factory.setJobFactory(jobFactory);
    factory.setQuartzProperties(quartzProperties);
    return factory;
  }

  @Bean
  public JobFactory jobFactory(ApplicationContext applicationContext) {
    AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
    jobFactory.setApplicationContext(applicationContext);
    return jobFactory;
  }

  /**
   * 配置JobFactory,为quartz作业提供Spring容器中bean的自动注入功能
   * <p>
   * https://blog.csdn.net/u012572955/article/details/51656270
   */
  public final class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory implements
      ApplicationContextAware {

    private transient AutowireCapableBeanFactory beanFactory;

    @Override
    public void setApplicationContext(final ApplicationContext context) {
      beanFactory = context.getAutowireCapableBeanFactory();
    }

    @Override
    protected Object createJobInstance(final TriggerFiredBundle bundle) throws Exception {
      final Object job = super.createJobInstance(bundle);
      beanFactory.autowireBean(job);
      return job;
    }

  }

}
