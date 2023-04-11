package com.gitee.dbswitch.admin.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncludeExcludeEnum {
  INCLUDE("精确包含"),
  EXCLUDE("精确排除"),
  ;

  private String name;
}
