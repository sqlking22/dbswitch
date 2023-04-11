package com.gitee.dbswitch.admin.logback;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogbackEventContent {

  private String identity;
  private String content;
}
