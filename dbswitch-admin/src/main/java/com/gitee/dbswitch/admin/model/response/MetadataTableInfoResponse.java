package com.gitee.dbswitch.admin.model.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("表元数据信息")
public class MetadataTableInfoResponse {

  private String tableName;
  private String schemaName;
  private String remarks;
  private String type;
}
