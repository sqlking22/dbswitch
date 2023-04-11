package com.gitee.dbswitch.admin.controller.privateapi;

import com.gitee.dbswitch.admin.common.annotation.TokenCheck;
import com.gitee.dbswitch.admin.common.response.PageResult;
import com.gitee.dbswitch.admin.common.response.Result;
import com.gitee.dbswitch.admin.config.SwaggerConfig;
import com.gitee.dbswitch.admin.model.response.MetadataSchemaDetailResponse;
import com.gitee.dbswitch.admin.model.response.MetadataTableDetailResponse;
import com.gitee.dbswitch.admin.model.response.MetadataTableInfoResponse;
import com.gitee.dbswitch.admin.model.response.SchemaTableDataResponse;
import com.gitee.dbswitch.admin.service.MetaDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"元数据查询接口"})
@RestController
@RequestMapping(value = SwaggerConfig.API_V1 + "/metadata")
public class MetaDataController {

  @Resource
  private MetaDataService metaDataService;

  @TokenCheck
  @ApiOperation(value = "模式列表")
  @GetMapping(value = "/schemas/{id}/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResult<MetadataSchemaDetailResponse> allSchemas(
      @PathVariable("id") Long id,
      @PathVariable(value = "page", required = false) Integer page,
      @PathVariable(value = "size", required = false) Integer size) {
    return metaDataService.allSchemas(id, page, size);
  }

  @TokenCheck
  @ApiOperation(value = "物理表/视图表列表")
  @GetMapping(value = "/tables/{id}/{page}/{size}", produces = MediaType.APPLICATION_JSON_VALUE)
  public PageResult<MetadataTableInfoResponse> allTables(
      @PathVariable("id") Long id,
      @RequestParam("schema") String schema,
      @PathVariable(value = "page", required = false) Integer page,
      @PathVariable(value = "size", required = false) Integer size) {
    return metaDataService.allTables(id, schema, page, size);
  }

  @TokenCheck
  @ApiOperation(value = "物理表/视图表信息")
  @GetMapping(value = "/meta/table/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<MetadataTableDetailResponse> tableMeta(
      @PathVariable("id") Long id,
      @RequestParam("schema") String schema,
      @RequestParam("table") String table) {
    return metaDataService.tableDetail(id, schema, table);
  }

  @TokenCheck
  @ApiOperation(value = "物理表/视图表的数据内容")
  @GetMapping(value = "/data/table/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Result<SchemaTableDataResponse> tableData(
      @PathVariable("id") Long id,
      @RequestParam("schema") String schema,
      @RequestParam("table") String table) {
    return metaDataService.tableData(id, schema, table);
  }

}
