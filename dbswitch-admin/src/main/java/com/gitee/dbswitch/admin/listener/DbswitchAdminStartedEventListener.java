package com.gitee.dbswitch.admin.listener;

import com.gitee.dbswitch.admin.dao.JobLogbackDAO;
import com.gitee.dbswitch.admin.logback.LogbackAppenderRegister;
import com.gitee.dbswitch.admin.logback.LogbackEventContent;
import com.gitee.dbswitch.admin.util.SpringUtils;
import java.util.Arrays;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

public class DbswitchAdminStartedEventListener implements ApplicationListener<ApplicationContextInitializedEvent> {

  private static final String[] LOGGER_CLASS_NAME = {
      "com.gitee.dbswitch.data.service.MigrationService",
      "com.gitee.dbswitch.data.handler.MigrationHandler"
  };

  @Override
  public void onApplicationEvent(ApplicationContextInitializedEvent event) {
    LogbackAppenderRegister.addDatabaseAppender(Arrays.asList(LOGGER_CLASS_NAME), this::recordLogContent);
  }

  private void recordLogContent(LogbackEventContent log) {
    SpringUtils.getBean(JobLogbackDAO.class).insert(log.getIdentity(), log.getContent());
  }

}