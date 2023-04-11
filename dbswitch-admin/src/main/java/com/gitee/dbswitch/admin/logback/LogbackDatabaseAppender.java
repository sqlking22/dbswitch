package com.gitee.dbswitch.admin.logback;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogbackDatabaseAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private String name;

  private Layout<ILoggingEvent> layout;

  private Consumer<LogbackEventContent> handler;

  public LogbackDatabaseAppender(Consumer<LogbackEventContent> handler) {
    this.handler = handler;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public Layout<ILoggingEvent> getLayout() {
    return layout;
  }

  public void setLayout(Layout<ILoggingEvent> layout) {
    this.layout = layout;
  }

  @Override
  public void start() {
    if (layout == null) {
      addError("LogbackDatabaseAppender layout cannot be null");
    }
    super.start();
  }

  @Override
  public void stop() {
    if (!isStarted()) {
      return;
    }
    super.stop();
  }

  @Override
  public void append(ILoggingEvent event) {
    if (event == null || !isStarted()) {
      return;
    }

    try {
      handler.accept(LogbackEventContent.builder().identity(name).content(layout.doLayout(event)).build());
    } catch (Exception e) {
      e.printStackTrace();
      log.error("failed to record logback log:{}", e.getMessage());
    }
  }

}
