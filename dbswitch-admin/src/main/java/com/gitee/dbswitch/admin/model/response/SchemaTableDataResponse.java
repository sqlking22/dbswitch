package com.gitee.dbswitch.admin.model.response;

import io.swagger.annotations.ApiModel;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@ApiModel("表数据内容")
public class SchemaTableDataResponse {

  private String schemaName;
  private String tableName;
  private List<String> columns;
  private List<Map<String,Object>> rows;
}
