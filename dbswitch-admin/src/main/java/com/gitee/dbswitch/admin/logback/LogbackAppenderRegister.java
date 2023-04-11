package com.gitee.dbswitch.admin.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.sift.MDCBasedDiscriminator;
import ch.qos.logback.classic.sift.SiftingAppender;
import java.util.List;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

@Slf4j
public final class LogbackAppenderRegister {

  public final static String LOG_MDC_KEY_NAME = "LogUuid";

  private final static String LOG_MDC_KEY_DEFAULT_VALUE = "00000000000";
  private final static String LOG_STORE_APPENDER_NAME = "SIFT-DATABASE";
  private final static String LOG_STORE_APPENDER_PATTERN
      = "[%d{yyyy-MM-dd HH:mm:ss.SSS}] [%thread] %-5level %logger{35} - %msg%n";

  public static void addDatabaseAppender(List<String> loggerClassNames,
      Consumer<LogbackEventContent> handler) {
    if (CollectionUtils.isEmpty(loggerClassNames)) {
      return;
    }

    try {
      LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

      MDCBasedDiscriminator discriminator = new MDCBasedDiscriminator();
      discriminator.setKey(LOG_MDC_KEY_NAME);
      discriminator.setDefaultValue(LOG_MDC_KEY_DEFAULT_VALUE);
      discriminator.start();

      SiftingAppender sa = new SiftingAppender();
      sa.setName(LOG_STORE_APPENDER_NAME);
      sa.setDiscriminator(discriminator);
      sa.setContext(loggerContext);
      sa.setAppenderFactory(
          (context, discriminatingValue) -> {
            PatternLayout layout = new PatternLayout();
            layout.setPattern(LOG_STORE_APPENDER_PATTERN);
            layout.setContext(context);
            layout.start();

            LogbackDatabaseAppender la = new LogbackDatabaseAppender(handler);
            la.setContext(context);
            la.setName(discriminatingValue);
            la.setLayout(layout);
            la.start();
            return la;
          }
      );
      sa.start();

      loggerClassNames.forEach(
          name -> {
            Logger jobLogger = loggerContext.getLogger(name);
            jobLogger.addAppender(sa);
            jobLogger.setLevel(Level.INFO);
            log.info("Success add and initialize appender of logback for class：{}", name);
          }
      );
    } catch (Exception e) {
      log.error("Failed add and initialize appender of logback ,message:", e);
    }
  }
}
