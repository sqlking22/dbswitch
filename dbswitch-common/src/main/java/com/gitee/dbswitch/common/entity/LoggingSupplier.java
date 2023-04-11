package com.gitee.dbswitch.common.entity;

import java.util.function.Supplier;

public class LoggingSupplier<T> extends AbstractLogging implements Supplier<T> {

  private final Supplier<T> command;

  public LoggingSupplier(Supplier<T> command, MdcKeyValue mdc) {
    super(mdc);
    this.command = command;
  }

  @Override
  public T get() {
    try {
      setupMdc();
      return command.get();
    } finally {
      cleanMdc();
    }
  }

}
