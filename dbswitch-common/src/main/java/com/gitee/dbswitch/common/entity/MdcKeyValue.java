package com.gitee.dbswitch.common.entity;

public class MdcKeyValue {

  private String mdcKey;
  private String mdcValue;

  public MdcKeyValue(String mdcKey, String mdcValue) {
    this.mdcKey = mdcKey;
    this.mdcValue = mdcValue;
  }

  public String getMdcKey() {
    return mdcKey;
  }

  public void setMdcKey(String mdcKey) {
    this.mdcKey = mdcKey;
  }

  public String getMdcValue() {
    return mdcValue;
  }

  public void setMdcValue(String mdcValue) {
    this.mdcValue = mdcValue;
  }
}
