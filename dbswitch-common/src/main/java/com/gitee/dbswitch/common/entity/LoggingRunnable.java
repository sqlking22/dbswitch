package com.gitee.dbswitch.common.entity;

public class LoggingRunnable extends AbstractLogging implements Runnable {

  private final Runnable command;

  public LoggingRunnable(Runnable command, MdcKeyValue mdc) {
    super(mdc);
    this.command = command;
  }

  @Override
  public void run() {
    try {
      setupMdc();
      command.run();
    } finally {
      cleanMdc();
    }
  }

}
