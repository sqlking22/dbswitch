package com.gitee.dbswitch.admin.model.request;

import com.gitee.dbswitch.common.entity.PatternMapper;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class PreviewColumnNameMapperRequest {

  private Long id;
  private String schemaName;
  private String tableName;
  private List<PatternMapper> nameMapper;
}
