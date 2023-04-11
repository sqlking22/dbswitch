package com.gitee.dbswitch.admin.model.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PreviewNameMapperResponse {

  private String originalName;
  private String targetName;
}
