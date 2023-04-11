package com.gitee.dbswitch.admin.model.response;

import io.swagger.annotations.ApiModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("字段元数据详情")
public class MetadataColumnDetailResponse {

  private String fieldName;
  private String typeName;
  private String typeClassName;
  private String fieldType;
  private String displaySize;
  private String scaleSize;
  private String precisionSize;
  private String isPrimaryKey;
  private String isAutoIncrement;
  private String isNullable;
  private String remarks;
}
