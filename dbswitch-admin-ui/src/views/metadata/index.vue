<template>
  <div>
    <el-card>
      <div class="flex-between">
        <div class="tree-container">
          <el-scrollbar style="height:100%">
            <el-tree class="scroller"
                     :props="props"
                     :load="loadNode"
                     :expand-on-click-node="true"
                     :highlight-current="true"
                     :render-content="renderContent"
                     @check-change="handleCheckChange"
                     @node-click="handleNodeClick"
                     lazy>
            </el-tree>
          </el-scrollbar>
        </div>
        <div class="table-container">
          <span>当前表：{{currentNode.schemaName}} / {{currentNode.tableName}}</span>
          <el-tabs v-model="activeName">
            <el-tab-pane label="基本信息"
                         name="first">
              <el-descriptions title="元数据"
                               size="small"
                               :column="1"
                               colon
                               border>
                <el-descriptions-item label="表名称">{{tableMeta.tableName}}</el-descriptions-item>
                <el-descriptions-item label="表类型">{{tableMeta.type}}</el-descriptions-item>
                <el-descriptions-item label="模式名">{{tableMeta.schemaName}}</el-descriptions-item>
                <el-descriptions-item label="表注释">
                  <el-input type="textarea"
                            :rows="2"
                            v-model="tableMeta.remarks"
                            auto-complete="off"
                            :readonly=true></el-input>
                </el-descriptions-item>
                <el-descriptions-item label="建表DDL">
                  <el-input type="textarea"
                            :rows="16"
                            v-model="tableMeta.createSql"
                            auto-complete="off"
                            :readonly=true></el-input>
                </el-descriptions-item>
              </el-descriptions>
            </el-tab-pane>
            <el-tab-pane label="字段信息"
                         name="seconde">
              <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                        :data="tableMeta.columns"
                        size="small"
                        border
                        style="width: 100%">
                <template slot="empty">
                  <span>单击左侧展开"数据源导航树"来查看表的元数据记录</span>
                </template>
                <el-table-column prop="fieldName"
                                 min-width="20%"
                                 show-overflow-tooltip
                                 label="名称">
                </el-table-column>
                <el-table-column prop="typeName"
                                 min-width="20%"
                                 label="类型">
                </el-table-column>
                <el-table-column prop="fieldType"
                                 min-width="7%"
                                 label="枚举值">
                </el-table-column>
                <el-table-column prop="displaySize"
                                 min-width="7%"
                                 label="长度">
                </el-table-column>
                <el-table-column prop="precision"
                                 min-width="5%"
                                 label="精确">
                </el-table-column>
                <el-table-column prop="scale"
                                 min-width="5%"
                                 label="位数">
                </el-table-column>
                <el-table-column prop="isPrimaryKey"
                                 min-width="5%"
                                 label="主键">
                </el-table-column>
                <el-table-column prop="isAutoIncrement"
                                 min-width="5%"
                                 label="自增">
                </el-table-column>
                <el-table-column prop="isNullable"
                                 min-width="5%"
                                 label="空">
                </el-table-column>
                <el-table-column prop="remarks"
                                 min-width="20%"
                                 show-overflow-tooltip
                                 label="注释">
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane class="table-container-data-table"
                         label="取样数据"
                         name="third">
              <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                        :data="sampleData.rows"
                        border>
                <template slot="empty">
                  <span>单击左侧展开"数据源导航树"来查看表的数据记录</span>
                </template>
                <el-table-column v-for="(item,index) in sampleData.columns"
                                 :prop="item"
                                 :label="item"
                                 :key="index"
                                 show-overflow-tooltip>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script>
import urlencode from "urlencode";

// 参考文章：https://blog.csdn.net/m0_50255772/article/details/109484828
export default {
  components: {},
  data () {
    return {
      props: {
        label: 'label',
        children: 'children',
        disabled: false,
        isLeaf: false
      },
      currentNode: {
        tableName: '-',
        schemaName: '-'
      },
      activeName: 'first',
      tableMeta: {
        tableName: '-',
        schemaName: '-',
        remarks: '',
        type: '-',
        createSql: "",
        primaryKeys: [],
        columns: []
      },
      count: 1,
      sampleData: []
    };
  },
  methods: {
    handleCheckChange (data, checked, indeterminate) {
      //console.log(data, checked, indeterminate);
    },
    handleNodeClick (data) {
      //console.log(data);
      var id = data.id;
      var schema = data.schemaName;
      var table = data.tableName;
      if (!data.hasChild && id && schema && table) {
        this.activeName = 'first';
        this.getTableMeta(id, schema, table);
        this.getTableData(id, schema, table);
      }
    },
    loadNode (node, resolve) {
      if (node.level === 0) {
        const rootNode = [{ label: '数据源导航树', value: 0, hasChild: true, children: 'child' }]
        return resolve(rootNode);
      }

      setTimeout(() => {
        if (node.level === 1) {
          this.loadConnectionList(resolve);
        } else if (node.level === 2) {
          this.loadSchemasList(resolve, node.data.value)
        } else if (node.level === 3) {
          this.loadTablesList(resolve, node.data.parent, node.data.label)
        } else if (node.level == 4) {
          resolve([]);
          //this.loadColumnList(resolve, node.data.id, node.data.parent, node.data.label)
        } else {
          resolve([]);
        }
      }, 500);
    },
    clearDataSet () {
      this.tableData = [];
      this.tableMeta = {
        tableName: '-',
        schemaName: '-',
        remarks: '',
        type: '-',
        createSql: "",
        primaryKeys: [],
        columns: []
      },
        this.sampleData = []
    },
    getTableMeta (id, schema, table) {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/meta/table/" + id + "?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list4:" + JSON.stringify(res.data.data))
            this.tableMeta = res.data.data;
            this.currentNode.tableName = table;
            this.currentNode.schemaName = schema;
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    async getTableData (id, schema, table) {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/data/table/" + id + "?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            this.sampleData = res.data.data;
            //console.log(this.sampleData)
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    loadConnectionList (resolve) {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/connection/list/name"
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list1:" + JSON.stringify(res.data.data))
            res.data.data.forEach(function (element) {
              element['label'] = element.name;
              element['parent'] = 0;
              element['value'] = element.id;
              element['hasChild'] = true;
              element['children'] = 'child';
            });
            return resolve(res.data.data);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    loadSchemasList (resolve, id) {
      //console.log("id=" + id);
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/schemas/" + id + "/1/0"
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list2:" + JSON.stringify(res.data.data))
            res.data.data.forEach(function (element) {
              element['label'] = element.schema;
              element['parent'] = id;
              element['value'] = element.connection;
              element['hasChild'] = true;
              element['children'] = 'child';
            });
            //this.tableData = res.data.data;
            return resolve(res.data.data);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    loadTablesList (resolve, id, schema) {
      //console.log("id=" + id + ",schema=" + schema);
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/tables/" + id + "/1/0?schema=" + urlencode(schema)
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list3:" + JSON.stringify(res.data.data))
            res.data.data.forEach(function (element) {
              element['label'] = element.tableName;
              element['parent'] = schema;
              element['id'] = id;
              element['value'] = element.type;
              element['hasChild'] = false;
              element['children'] = 'child';
            });
            //this.tableData = res.data.data;
            return resolve(res.data.data);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    loadColumnList (resolve, id, schema, table) {
      //console.log("id=" + id);
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/metadata/columns/" + id + "/1/0?schema=" + urlencode(schema) + "&table=" + urlencode(table)
      }).then(
        res => {
          if (0 === res.data.code) {
            //console.log("list3:" + JSON.stringify(res.data.data))
            res.data.data.forEach(function (element) {
              element['label'] = element.fieldName.name;
              element['parent'] = schema;
              element['value'] = element.fieldName.value;
              element['hasChild'] = false;
              element['children'] = 'child';
            });
            this.tableData = res.data.data;
            return resolve([]);
          } else {
            this.$alert("加载失败，原因：" + res.data.message, '数据加载失败');
            this.clearDataSet();
          }
        }
      );
    },
    renderContent (h, { node, data, store }) {
      if (node.level === 1) {
        return (
          <div class="custom-tree-node">
            <i class="el-icon-takeaway-box"></i>
            <span>{data.label}</span>
          </div>
        );
      } else if (node.level === 2) {
        return (
          <div class="custom-tree-node">
            <i class="el-icon-folder-opened"></i>
            <span>{data.label}</span>
          </div>
        );
      } else if (node.level === 3) {
        return (
          <div class="custom-tree-node">
            <i class="iconfont icon-shujuku1"></i>
            <span>{data.label}</span>
          </div>
        );
      } else {
        var icon_pic = "iconfont icon-shitu_biaoge";
        if (data.value === 'VIEW') {
          icon_pic = "iconfont icon-viewList"
        }

        return (
          <div class="custom-tree-node">
            <i class={icon_pic}></i>
            <el-tooltip class="item" effect="light" placement="left">
              <div slot="content">{node.label}</div>
              <span>{data.label}</span>
            </el-tooltip>
          </div>
        );
      }

    }
  }
}
</script>

<style scoped>
.el-card {
  width: 100%;
  height: 100%;
}

.el-message {
  width: 100%;
  height: 100%;
  overflow: auto;
}
.flex-between {
  display: flex;
}
.el-scrollbar .el-scrollbar__wrap {overflow-x: hidden;}
.tree-container .el-tree {
  min-width: 350px;
  position: relative;
  cursor: default;
  background: #f3f1f1;
}
.scroller {
  min-width: 100%;
}
.tree-container .tree {
  overflow: auto;
  max-height: 200px;
}
.table-container {
  width: 80%;
  padding: 10px;
}
.table-container-data-table {
  height: 90%;
  overflow-y: auto;
  overflow-x: hidden;
}
</style>
