// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.data.handler;

import com.gitee.dbswitch.common.entity.CloseableDataSource;
import com.gitee.dbswitch.common.type.ProductTypeEnum;
import com.gitee.dbswitch.common.util.DatabaseAwareUtils;
import com.gitee.dbswitch.common.util.PatterNameUtils;
import com.gitee.dbswitch.core.model.ColumnDescription;
import com.gitee.dbswitch.core.model.TableDescription;
import com.gitee.dbswitch.core.service.IMetaDataByDatasourceService;
import com.gitee.dbswitch.core.service.impl.MetaDataByDataSourceServiceImpl;
import com.gitee.dbswitch.data.config.DbswichProperties;
import com.gitee.dbswitch.data.entity.SourceDataSourceProperties;
import com.gitee.dbswitch.data.util.BytesUnitUtils;
import com.gitee.dbswitch.dbchange.ChangeCalculatorService;
import com.gitee.dbswitch.dbchange.IDatabaseChangeCalculator;
import com.gitee.dbswitch.dbchange.IDatabaseRowHandler;
import com.gitee.dbswitch.dbchange.RecordChangeTypeEnum;
import com.gitee.dbswitch.dbchange.TaskParamEntity;
import com.gitee.dbswitch.dbcommon.database.DatabaseOperatorFactory;
import com.gitee.dbswitch.dbcommon.database.IDatabaseOperator;
import com.gitee.dbswitch.dbcommon.domain.StatementResultSet;
import com.gitee.dbswitch.dbsynch.DatabaseSynchronizeFactory;
import com.gitee.dbswitch.dbsynch.IDatabaseSynchronize;
import com.gitee.dbswitch.dbwriter.DatabaseWriterFactory;
import com.gitee.dbswitch.dbwriter.IDatabaseWriter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.ehcache.sizeof.SizeOf;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

/**
 * 在一个线程内的单表迁移处理逻辑
 *
 * @author tang
 */
@Slf4j
public class MigrationHandler implements Supplier<Long> {

  private final long MAX_CACHE_BYTES_SIZE = 128 * 1024 * 1024;

  private int fetchSize = 100;
  private final DbswichProperties properties;
  private final SourceDataSourceProperties sourceProperties;

  private volatile boolean interrupted = false;

  // 来源端
  private final CloseableDataSource sourceDataSource;
  private ProductTypeEnum sourceProductType;
  private String sourceSchemaName;
  private String sourceTableName;
  private String sourceTableRemarks;
  private List<ColumnDescription> sourceColumnDescriptions;
  private List<String> sourcePrimaryKeys;

  private IMetaDataByDatasourceService sourceMetaDataService;

  // 目的端
  private final CloseableDataSource targetDataSource;
  private ProductTypeEnum targetProductType;
  private Set<String> targetExistTables;
  private String targetSchemaName;
  private String targetTableName;
  private List<ColumnDescription> targetColumnDescriptions;
  private List<String> targetPrimaryKeys;

  // 日志输出字符串使用
  private String tableNameMapString;

  public static MigrationHandler createInstance(TableDescription td,
      DbswichProperties properties,
      Integer sourcePropertiesIndex,
      CloseableDataSource sds,
      CloseableDataSource tds,
      Set<String> targetExistTables) {
    return new MigrationHandler(td, properties, sourcePropertiesIndex, sds, tds, targetExistTables);
  }

  private MigrationHandler(TableDescription td,
      DbswichProperties properties,
      Integer sourcePropertiesIndex,
      CloseableDataSource sds,
      CloseableDataSource tds,
      Set<String> targetExistTables) {
    this.sourceSchemaName = td.getSchemaName();
    this.sourceTableName = td.getTableName();
    this.properties = properties;
    this.sourceProperties = properties.getSource().get(sourcePropertiesIndex);
    this.sourceDataSource = sds;
    this.targetDataSource = tds;

    if (sourceProperties.getFetchSize() >= fetchSize) {
      fetchSize = sourceProperties.getFetchSize();
    }

    this.targetExistTables = targetExistTables;
    // 获取映射转换后新的表名
    this.targetSchemaName = properties.getTarget().getTargetSchema();
    this.targetTableName = PatterNameUtils.getFinalName(td.getTableName(),
        sourceProperties.getRegexTableMapper());

    if (StringUtils.isEmpty(this.targetTableName)) {
      throw new RuntimeException("表名的映射规则配置有误，不能将[" + this.sourceTableName + "]映射为空");
    }

    this.tableNameMapString = String.format("%s.%s --> %s.%s",
        td.getSchemaName(), td.getTableName(),
        targetSchemaName, targetTableName);
  }

  public void interrupt() {
    this.interrupted = true;
  }

  @Override
  public Long get() {
    log.info("Begin Migrate table for {}", tableNameMapString);

    this.sourceProductType = DatabaseAwareUtils.getDatabaseTypeByDataSource(sourceDataSource);
    this.targetProductType = DatabaseAwareUtils.getDatabaseTypeByDataSource(targetDataSource);
    this.sourceMetaDataService = new MetaDataByDataSourceServiceImpl(sourceDataSource,
        sourceProductType);

    // 读取源表的表及字段元数据
    this.sourceTableRemarks = sourceMetaDataService
        .getTableRemark(sourceSchemaName, sourceTableName);
    this.sourceColumnDescriptions = sourceMetaDataService
        .queryTableColumnMeta(sourceSchemaName, sourceTableName);
    this.sourcePrimaryKeys = sourceMetaDataService
        .queryTablePrimaryKeys(sourceSchemaName, sourceTableName);

    // 根据表的列名映射转换准备目标端表的字段信息
    this.targetColumnDescriptions = sourceColumnDescriptions.stream()
        .map(column -> {
          String newName = PatterNameUtils.getFinalName(
              column.getFieldName(),
              sourceProperties.getRegexColumnMapper());
          ColumnDescription description = column.copy();
          description.setFieldName(newName);
          description.setLabelName(newName);
          return description;
        }).collect(Collectors.toList());
    this.targetPrimaryKeys = sourcePrimaryKeys.stream()
        .map(name ->
            PatterNameUtils.getFinalName(name, sourceProperties.getRegexColumnMapper())
        ).collect(Collectors.toList());

    // 打印表名与字段名的映射关系
    List<String> columnMapperPairs = new ArrayList<>();
    Map<String, String> mapChecker = new HashMap<>();
    for (int i = 0; i < sourceColumnDescriptions.size(); ++i) {
      String sourceColumnName = sourceColumnDescriptions.get(i).getFieldName();
      String targetColumnName = targetColumnDescriptions.get(i).getFieldName();
      if (StringUtils.hasLength(targetColumnName)) {
        columnMapperPairs.add(String.format("%s --> %s", sourceColumnName, targetColumnName));
        mapChecker.put(sourceColumnName, targetColumnName);
      } else {
        columnMapperPairs.add(String.format(
            "%s --> %s",
            sourceColumnName,
            String.format("<!Field(%s) is Deleted>", (i + 1))
        ));
      }
    }
    log.info("Mapping relation : \ntable mapper :\n\t{}  \ncolumn mapper :\n\t{} ",
        tableNameMapString, String.join("\n\t", columnMapperPairs));
    Set<String> valueSet = new HashSet<>(mapChecker.values());
    if (valueSet.size() <= 0) {
      throw new RuntimeException("字段映射配置有误，禁止通过映射将表所有的字段都删除!");
    }
    if (!valueSet.containsAll(this.targetPrimaryKeys)) {
      throw new RuntimeException("字段映射配置有误，禁止通过映射将表的主键字段删除!");
    }
    if (mapChecker.keySet().size() != valueSet.size()) {
      throw new RuntimeException("字段映射配置有误，禁止将多个字段映射到一个同名字段!");
    }
    if (interrupted) {
      log.info("task job is interrupted!");
      throw new RuntimeException("task is interrupted");
    }
    IDatabaseWriter writer = DatabaseWriterFactory.createDatabaseWriter(
        targetDataSource, properties.getTarget().getWriterEngineInsert());

    if (properties.getTarget().getTargetDrop()) {
      /*
        如果配置了dbswitch.target.datasource-target-drop=true时，
        <p>
        先执行drop table语句，然后执行create table语句
       */

      try {
        DatabaseOperatorFactory.createDatabaseOperator(targetDataSource)
            .dropTable(targetSchemaName, targetTableName);
        log.info("Target Table {}.{} is exits, drop it now !", targetSchemaName, targetTableName);
      } catch (Exception e) {
        log.info("Target Table {}.{} is not exits, create it!", targetSchemaName, targetTableName);
      }

      // 生成建表语句并创建
      List<String> sqlCreateTable = sourceMetaDataService.getDDLCreateTableSQL(
          targetProductType,
          targetColumnDescriptions.stream()
              .filter(column -> StringUtils.hasLength(column.getFieldName()))
              .collect(Collectors.toList()),
          targetPrimaryKeys,
          targetSchemaName,
          targetTableName,
          sourceTableRemarks,
          properties.getTarget().getCreateTableAutoIncrement()
      );

      JdbcTemplate targetJdbcTemplate = new JdbcTemplate(targetDataSource);
      for (String sql : sqlCreateTable) {
        targetJdbcTemplate.execute(sql);
        log.info("Execute SQL: \n{}", sql);
      }

      // 如果只想创建表，这里直接返回
      if (null != properties.getTarget().getOnlyCreate()
          && properties.getTarget().getOnlyCreate()) {
        return 0L;
      }

      if (interrupted) {
        log.info("task job is interrupted!");
        throw new RuntimeException("task is interrupted");
      }

      return doFullCoverSynchronize(writer);
    } else {
      // 对于只想创建表的情况，不提供后续的变化量数据同步功能
      if (null != properties.getTarget().getOnlyCreate()
          && properties.getTarget().getOnlyCreate()) {
        return 0L;
      }

      if (interrupted) {
        log.info("task job is interrupted!");
        throw new RuntimeException("task is interrupted");
      }

      if (!targetExistTables.contains(targetTableName)) {
        // 当目标端不存在该表时，则生成建表语句并创建
        List<String> sqlCreateTable = sourceMetaDataService.getDDLCreateTableSQL(
            targetProductType,
            targetColumnDescriptions.stream()
                .filter(column -> StringUtils.hasLength(column.getFieldName()))
                .collect(Collectors.toList()),
            targetPrimaryKeys,
            targetSchemaName,
            targetTableName,
            sourceTableRemarks,
            properties.getTarget().getCreateTableAutoIncrement()
        );

        JdbcTemplate targetJdbcTemplate = new JdbcTemplate(targetDataSource);
        for (String sql : sqlCreateTable) {
          targetJdbcTemplate.execute(sql);
          log.info("Execute SQL: \n{}", sql);
        }

        if (interrupted) {
          log.info("task job is interrupted!");
          throw new RuntimeException("task is interrupted");
        }

        return doFullCoverSynchronize(writer);
      }

      // 判断是否具备变化量同步的条件：（1）两端表结构一致，且都有一样的主键字段；(2)MySQL使用Innodb引擎；
      if (properties.getTarget().getChangeDataSync()) {
        // 根据主键情况判断同步的方式：增量同步或覆盖同步
        IMetaDataByDatasourceService metaDataByDatasourceService =
            new MetaDataByDataSourceServiceImpl(targetDataSource, targetProductType);
        List<String> dbTargetPks = metaDataByDatasourceService.queryTablePrimaryKeys(
            targetSchemaName, targetTableName);

        if (!targetPrimaryKeys.isEmpty() && !dbTargetPks.isEmpty()
            && targetPrimaryKeys.containsAll(dbTargetPks)
            && dbTargetPks.containsAll(targetPrimaryKeys)) {
          if (targetProductType == ProductTypeEnum.MYSQL
              && !DatabaseAwareUtils.isMysqlInnodbStorageEngine(
              targetSchemaName, targetTableName, targetDataSource)) {
            return doFullCoverSynchronize(writer);
          } else {
            return doIncreaseSynchronize(writer);
          }
        } else {
          return doFullCoverSynchronize(writer);
        }
      } else {
        return doFullCoverSynchronize(writer);
      }
    }
  }

  /**
   * 执行覆盖同步
   *
   * @param writer 目的端的写入器
   */
  private Long doFullCoverSynchronize(IDatabaseWriter writer) {
    final int BATCH_SIZE = fetchSize;

    List<String> sourceFields = new ArrayList<>();
    List<String> targetFields = new ArrayList<>();
    for (int i = 0; i < targetColumnDescriptions.size(); ++i) {
      ColumnDescription scd = sourceColumnDescriptions.get(i);
      ColumnDescription tcd = targetColumnDescriptions.get(i);
      if (!StringUtils.isEmpty(tcd.getFieldName())) {
        sourceFields.add(scd.getFieldName());
        targetFields.add(tcd.getFieldName());
      }
    }
    // 准备目的端的数据写入操作
    writer.prepareWrite(targetSchemaName, targetTableName, targetFields);

    // 清空目的端表的数据
    IDatabaseOperator targetOperator = DatabaseOperatorFactory
        .createDatabaseOperator(writer.getDataSource());
    targetOperator.truncateTableData(targetSchemaName, targetTableName);

    // 查询源端数据并写入目的端
    IDatabaseOperator sourceOperator = DatabaseOperatorFactory
        .createDatabaseOperator(sourceDataSource);
    sourceOperator.setFetchSize(BATCH_SIZE);

    StatementResultSet srs = sourceOperator.queryTableData(
        sourceSchemaName, sourceTableName, sourceFields
    );

    List<Object[]> cache = new LinkedList<>();
    long cacheBytes = 0;
    long totalCount = 0;
    long totalBytes = 0;
    try (ResultSet rs = srs.getResultset()) {
      while (rs.next()) {
        if (interrupted) {
          log.info("task job is interrupted!");
          throw new RuntimeException("task is interrupted");
        }
        Object[] record = new Object[sourceFields.size()];
        for (int i = 1; i <= sourceFields.size(); ++i) {
          try {
            record[i - 1] = rs.getObject(i);
          } catch (Exception e) {
            log.warn("!!! Read data from table [ {} ] use function ResultSet.getObject() error",
                tableNameMapString, e);
            record[i - 1] = null;
          }
        }

        cache.add(record);
        long bytes = SizeOf.newInstance().deepSizeOf(record);
        cacheBytes += bytes;
        ++totalCount;

        if (cache.size() >= BATCH_SIZE || cacheBytes >= MAX_CACHE_BYTES_SIZE) {
          long ret = writer.write(targetFields, cache);
          log.info("[FullCoverSync] handle table [{}] data count: {}, the batch bytes sie: {}",
              tableNameMapString, ret, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
          cache.clear();
          totalBytes += cacheBytes;
          cacheBytes = 0;
        }
      }

      if (cache.size() > 0) {
        long ret = writer.write(targetFields, cache);
        log.info("[FullCoverSync] handle table [{}] data count: {}, last batch bytes sie: {}",
            tableNameMapString, ret, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
        cache.clear();
        totalBytes += cacheBytes;
      }

      log.info("[FullCoverSync] handle table [{}] total data count:{}, total bytes={}",
          tableNameMapString, totalCount, BytesUnitUtils.bytesSizeToHuman(totalBytes));
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      srs.close();
    }

    return totalBytes;
  }

  /**
   * 变化量同步
   *
   * @param writer 目的端的写入器
   */
  private Long doIncreaseSynchronize(IDatabaseWriter writer) {
    final int BATCH_SIZE = fetchSize;

    List<String> sourceFields = new ArrayList<>();
    List<String> targetFields = new ArrayList<>();
    Map<String, String> columnNameMaps = new HashMap<>();
    for (int i = 0; i < targetColumnDescriptions.size(); ++i) {
      ColumnDescription scd = sourceColumnDescriptions.get(i);
      ColumnDescription tcd = targetColumnDescriptions.get(i);
      if (!StringUtils.isEmpty(tcd.getFieldName())) {
        sourceFields.add(scd.getFieldName());
        targetFields.add(tcd.getFieldName());
        columnNameMaps.put(scd.getFieldName(), tcd.getFieldName());
      }
    }

    TaskParamEntity.TaskParamEntityBuilder taskBuilder = TaskParamEntity.builder();
    taskBuilder.oldDataSource(writer.getDataSource());
    taskBuilder.oldSchemaName(targetSchemaName);
    taskBuilder.oldTableName(targetTableName);
    taskBuilder.newDataSource(sourceDataSource);
    taskBuilder.newSchemaName(sourceSchemaName);
    taskBuilder.newTableName(sourceTableName);
    taskBuilder.fieldColumns(sourceFields);
    taskBuilder.columnsMap(columnNameMaps);

    TaskParamEntity param = taskBuilder.build();

    IDatabaseSynchronize synchronizer = DatabaseSynchronizeFactory
        .createDatabaseWriter(writer.getDataSource());
    synchronizer.prepare(targetSchemaName, targetTableName, targetFields, targetPrimaryKeys);

    IDatabaseChangeCalculator calculator = new ChangeCalculatorService();
    calculator.setFetchSize(fetchSize);
    calculator.setRecordIdentical(false);
    calculator.setCheckJdbcType(false);

    AtomicLong totalBytes = new AtomicLong(0);

    // 执行实际的变化同步过程
    calculator.executeCalculate(param, new IDatabaseRowHandler() {

      private long countInsert = 0;
      private long countUpdate = 0;
      private long countDelete = 0;
      private long countTotal = 0;
      private long cacheBytes = 0;
      private final List<Object[]> cacheInsert = new LinkedList<>();
      private final List<Object[]> cacheUpdate = new LinkedList<>();
      private final List<Object[]> cacheDelete = new LinkedList<>();

      @Override
      public void handle(List<String> fields, Object[] record, RecordChangeTypeEnum flag) {
        if (flag == RecordChangeTypeEnum.VALUE_INSERT) {
          cacheInsert.add(record);
          countInsert++;
        } else if (flag == RecordChangeTypeEnum.VALUE_CHANGED) {
          cacheUpdate.add(record);
          countUpdate++;
        } else {
          cacheDelete.add(record);
          countDelete++;
        }

        long bytes = SizeOf.newInstance().deepSizeOf(record);
        cacheBytes += bytes;
        totalBytes.addAndGet(bytes);
        countTotal++;
        checkFull(fields);
      }

      /**
       * 检测缓存是否已满，如果已满执行同步操作
       *
       * @param fields 同步的字段列表
       */
      private void checkFull(List<String> fields) {
        if (interrupted) {
          log.info("task job is interrupted!");
          throw new RuntimeException("task is interrupted");
        }
        if (cacheInsert.size() >= BATCH_SIZE || cacheUpdate.size() >= BATCH_SIZE
            || cacheDelete.size() >= BATCH_SIZE || cacheBytes >= MAX_CACHE_BYTES_SIZE) {
          if (cacheDelete.size() > 0) {
            doDelete(fields);
          }

          if (cacheInsert.size() > 0) {
            doInsert(fields);
          }

          if (cacheUpdate.size() > 0) {
            doUpdate(fields);
          }

          log.info("[IncreaseSync] Handle table [{}] data one batch size: {}",
              tableNameMapString, BytesUnitUtils.bytesSizeToHuman(cacheBytes));
          cacheBytes = 0;
        }
      }

      @Override
      public void destroy(List<String> fields) {
        if (cacheDelete.size() > 0) {
          doDelete(fields);
        }

        if (cacheInsert.size() > 0) {
          doInsert(fields);
        }

        if (cacheUpdate.size() > 0) {
          doUpdate(fields);
        }

        log.info("[IncreaseSync] Handle table [{}] total count: {}, Insert:{},Update:{},Delete:{} ",
            tableNameMapString, countTotal, countInsert, countUpdate, countDelete);
      }

      private void doInsert(List<String> fields) {
        long ret = synchronizer.executeInsert(cacheInsert);
        log.info("[IncreaseSync] Handle table [{}] data Insert count: {}", tableNameMapString, ret);
        cacheInsert.clear();
      }

      private void doUpdate(List<String> fields) {
        long ret = synchronizer.executeUpdate(cacheUpdate);
        log.info("[IncreaseSync] Handle table [{}] data Update count: {}", tableNameMapString, ret);
        cacheUpdate.clear();
      }

      private void doDelete(List<String> fields) {
        long ret = synchronizer.executeDelete(cacheDelete);
        log.info("[IncreaseSync] Handle table [{}] data Delete count: {}", tableNameMapString, ret);
        cacheDelete.clear();
      }

    });

    return totalBytes.get();
  }

}
