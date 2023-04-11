package com.gitee.dbswitch.admin.model.response;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("模式元数据详情")
public class MetadataSchemaDetailResponse {
  
  private String connection;
  private String schema;
}
