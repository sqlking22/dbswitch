package com.gitee.dbswitch.admin.config;

import com.gitee.dbswitch.data.util.DataSourceUtils;
import java.util.concurrent.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration("dbswitchExecutorConfig")
public class ExecutorConfig {

  public final static String TASK_EXECUTOR_BEAN_NAME = "migrationTaskExecutor";

  /**
   * 创建一个异步任务执行ThreadPoolTaskExecutor
   *
   * @return ThreadPoolTaskExecutor
   */
  @Bean(TASK_EXECUTOR_BEAN_NAME)
  public AsyncTaskExecutor createTableMigrationTaskExecutor() {
    ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
    taskExecutor.setCorePoolSize(DataSourceUtils.MAX_THREAD_COUNT);
    taskExecutor.setMaxPoolSize(DataSourceUtils.MAX_THREAD_COUNT);
    taskExecutor.setQueueCapacity(10000);
    taskExecutor.setKeepAliveSeconds(1800);
    taskExecutor.setDaemon(true);
    taskExecutor.setThreadGroupName("dbswitch");
    taskExecutor.setThreadNamePrefix("dbswitch-migration-");
    taskExecutor.setBeanName(TASK_EXECUTOR_BEAN_NAME);
    taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    taskExecutor.initialize();
    return taskExecutor;
  }
}
