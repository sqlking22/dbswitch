// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.service;

import cn.hutool.core.exceptions.ExceptionUtil;
import com.gitee.dbswitch.admin.dao.AssignmentConfigDAO;
import com.gitee.dbswitch.admin.dao.AssignmentJobDAO;
import com.gitee.dbswitch.admin.dao.AssignmentTaskDAO;
import com.gitee.dbswitch.admin.entity.AssignmentConfigEntity;
import com.gitee.dbswitch.admin.entity.AssignmentJobEntity;
import com.gitee.dbswitch.admin.entity.AssignmentTaskEntity;
import com.gitee.dbswitch.admin.logback.LogbackAppenderRegister;
import com.gitee.dbswitch.common.entity.MdcKeyValue;
import com.gitee.dbswitch.admin.type.JobStatusEnum;
import com.gitee.dbswitch.data.util.JsonUtils;
import com.gitee.dbswitch.data.config.DbswichProperties;
import com.gitee.dbswitch.data.service.MigrationService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * <p>
 * 如果你使用了@PersistJobDataAfterExecution注解，则强烈建议你同时使用@DisallowConcurrentExecution注解，
 * <p>
 * 因为当同一个job（JobDetail）的两个实例被并发执行时，由于竞争，JobDataMap中存储的数据很可能是不确定的。
 * <p>
 */
@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobExecutorService extends QuartzJobBean implements InterruptableJob {

  public final static String GROUP = "dbswitch";
  public final static String TASK_ID = "taskId";
  public final static String SCHEDULE = "schedule";

  private final static String MDC_KEY = LogbackAppenderRegister.LOG_MDC_KEY_NAME;

  // 相同taskId的任务限制并发执行的粒度锁缓存对象
  private static Cache<String, ReentrantLock> mutexes = CacheBuilder.newBuilder()
      .expireAfterWrite(24 * 60L, TimeUnit.MINUTES)
      .build();

  /**
   * 作为一个是否被中断的标识
   */
  private volatile boolean interrupted = false;

  /**
   * 记录当前线程
   */
  private Thread currentThread;

  /**
   * 因为在QuartzConfig中进行了注入配置，所以 Quartz会将数据注入到jobKey变量中
   */
  private String taskId;

  /**
   * 迁移服务类
   */
  private MigrationService migrationService;

  /**
   * 这里可以使用Spring容器中的bean进行注入
   */
  @Resource
  private AssignmentTaskDAO assignmentTaskDAO;

  @Resource
  private AssignmentConfigDAO assignmentConfigDAO;

  @Resource
  private AssignmentJobDAO assignmentJobDAO;

  @Resource
  private AsyncTaskExecutor migrationTaskExecutor;

  /**
   * 实现setter方法，Quartz会给成员变量taskId注入值
   *
   * @param taskId Task的ID
   */
  public void setTaskId(String taskId) {
    this.taskId = taskId;
  }

  @Override
  public void interrupt() throws UnableToInterruptJobException {
    log.info("Quartz Schedule Task job is interrupting : taskId={} ", taskId);
    interrupted = true;
    if (Objects.nonNull(migrationService)) {
      migrationService.interrupt();
    }
    currentThread.interrupt();
  }

  @Override
  public void executeInternal(JobExecutionContext context) throws JobExecutionException {
    currentThread = Thread.currentThread();
    JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
    if (interrupted) {
      log.info("Quartz task id:{} interrupted when thread begin", jobDataMap.getLong(TASK_ID));
      return;
    }

    JobKey key = context.getJobDetail().getKey();
    Long taskId = jobDataMap.getLongValue(TASK_ID);
    Integer schedule = jobDataMap.getIntValue(SCHEDULE);
    AssignmentJobEntity assignmentJobEntity = assignmentJobDAO
        .newAssignmentJob(taskId, schedule, key.getName());
    MdcKeyValue mdcKeyValue = new MdcKeyValue(MDC_KEY, assignmentJobEntity.getId().toString());

    try {
      ReentrantLock lock = mutexes.get(taskId.toString(), ReentrantLock::new);
      while (!lock.tryLock(1, TimeUnit.SECONDS)) {
        if (interrupted) {
          log.info("Quartz task id:{} interrupted when get lock", jobDataMap.getLong(TASK_ID));
          return;
        }
        TimeUnit.SECONDS.sleep(1);
      }

      try {
        log.info("Execute Quartz Job, and task id is : {} , job id is: {}", taskId,
            assignmentJobEntity.getId());

        AssignmentTaskEntity task = assignmentTaskDAO.getById(taskId);
        AssignmentConfigEntity assignmentConfigEntity = assignmentConfigDAO
            .getByAssignmentTaskId(task.getId());

        log.info("Execute Assignment [taskId={}],Task Name: {} ,configuration properties：{}",
            task.getId(),
            task.getName(),
            task.getContent());

        try {
          DbswichProperties properties = JsonUtils.toBeanObject(
              task.getContent(), DbswichProperties.class);
          if (!assignmentConfigEntity.getFirstFlag()) {
            if (!assignmentConfigEntity.getTargetOnlyCreate()) {
              properties.getTarget().setTargetDrop(false);
              properties.getTarget().setOnlyCreate(false);
              properties.getTarget().setChangeDataSync(true);
            }
          }
          if (assignmentConfigEntity.getTargetOnlyCreate()) {
            properties.getTarget().setTargetDrop(true);
          }

          migrationService = new MigrationService(properties, migrationTaskExecutor);
          if (interrupted) {
            log.info("Quartz task id:{} interrupted when prepare stage", jobDataMap.getLong(TASK_ID));
            return;
          }

          // 实际执行JOB
          migrationService.setMdcKeyValue(mdcKeyValue);
          migrationService.run();

          if (assignmentConfigEntity.getFirstFlag()) {
            AssignmentConfigEntity config = new AssignmentConfigEntity();
            config.setId(assignmentConfigEntity.getId());
            config.setTargetDropTable(assignmentConfigEntity.getTargetOnlyCreate());
            config.setFirstFlag(Boolean.FALSE);
            assignmentConfigDAO.updateSelective(config);
          }

          assignmentJobEntity.setStatus(JobStatusEnum.PASS.getValue());
          log.info("Execute Assignment Success [taskId={},jobId={}],Task Name: {}",
              task.getId(), assignmentJobEntity.getId(), task.getName());
        } catch (Throwable e) {
          assignmentJobEntity.setStatus(JobStatusEnum.FAIL.getValue());
          assignmentJobEntity.setErrorLog(ExceptionUtil.stacktraceToString(e));
          log.info("Execute Assignment Failed [taskId={},jobId={}],Task Name: {}",
              task.getId(), assignmentJobEntity.getId(), task.getName(), e);
        } finally {
          assignmentJobEntity.setFinishTime(new Timestamp(System.currentTimeMillis()));
          assignmentJobDAO.updateSelective(assignmentJobEntity);
        }
      } finally {
        lock.unlock();
      }
    } catch (ExecutionException | InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

}
