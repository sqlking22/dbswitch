package com.gitee.dbswitch.common.entity;

import java.util.Objects;
import org.slf4j.MDC;

public abstract class AbstractLogging {

  private final MdcKeyValue mdc;

  public AbstractLogging(MdcKeyValue mdc) {
    this.mdc = Objects.requireNonNull(mdc, "mdc is null");
  }

  protected void setupMdc() {
    MDC.put(mdc.getMdcKey(), mdc.getMdcValue());
  }

  protected void cleanMdc() {
    MDC.remove(mdc.getMdcKey());
  }
}
