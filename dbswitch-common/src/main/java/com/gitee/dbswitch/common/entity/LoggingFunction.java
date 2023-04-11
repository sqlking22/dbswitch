package com.gitee.dbswitch.common.entity;

import java.util.function.Function;

public class LoggingFunction<T, R> extends AbstractLogging implements Function<T, R> {

  private final Function<T, R> command;

  public LoggingFunction(Function<T, R> command, MdcKeyValue mdc) {
    super(mdc);
    this.command = command;
  }

  @Override
  public R apply(T t) {
    try {
      setupMdc();
      return command.apply(t);
    } finally {
      cleanMdc();
    }
  }

}
