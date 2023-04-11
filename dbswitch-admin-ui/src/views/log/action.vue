<template>
  <div style="margin-top: 15px">
    <el-table v-loading="loading"
              :header-cell-style="{background:'#eef1f6',color:'#606266'}"
              element-loading-text="拼命加载中"
              element-loading-spinner="el-icon-loading"
              element-loading-background="rgba(0, 0, 0, 0.8)"
              :data="lists"
              stripe
              style="width:100%"
              size="small"
              border>
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left"
                   inline
                   class="demo-table-expand">
            <el-form-item label="日志编号:">
              <span>{{ props.row.id }}</span>
            </el-form-item>
            <el-form-item label="日志时间:">
              <span>{{ props.row.createTime }}</span>
            </el-form-item>
            <el-form-item label="操作用户:">
              <span>{{ props.row.username }}</span>
            </el-form-item>
            <el-form-item label="请求IP地址:">
              <span>{{ props.row.ipAddress }}</span>
            </el-form-item>
            <el-form-item label="操作模块:">
              <span>{{ props.row.moduleName }}</span>
            </el-form-item>
            <el-form-item label="操作描述:">
              <span>{{ props.row.content }}</span>
            </el-form-item>
            <el-form-item label="处理耗时(ms):">
              <span>{{ props.row.elapseSeconds }}</span>
            </el-form-item>
            <el-form-item label="请求路径:">
              <span>{{ props.row.urlPath }}</span>
            </el-form-item>
            <el-form-item label="异常状态:">
              <span>{{ props.row.failed }}</span>
            </el-form-item>
            <el-form-item label="">
              <span></span>
            </el-form-item>
            <el-form-item label="异常日志:">
              <el-input type="textarea"
                        style="font-size:12px;width: 700px"
                        :autosize="{ minRows: 2, maxRows: 5}"
                        v-model="props.row.exception">
              </el-input>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column prop="createTime"
                       label="日志时间"
                       min-width="15%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="username"
                       label="操作用户"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="ipAddress"
                       label="请求IP"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="moduleName"
                       label="操作类型"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="elapseSeconds"
                       label="耗时(ms)"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="urlPath"
                       label="请求路径"
                       min-width="20%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="failed"
                       label="异常"
                       :formatter="boolFormat"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
    </el-table>
    <div class="page"
         align="right">
      <el-pagination @size-change="handleSizeChange"
                     @current-change="handleCurrentChange"
                     :current-page="currentPage"
                     :page-sizes="[5, 10, 20, 40]"
                     :page-size="pageSize"
                     layout="total, sizes, prev, pager, next, jumper"
                     :total="totalCount"></el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  data () {
    return {
      loading: true,
      lists: [],
      currentPage: 1,
      pageSize: 10,
      totalCount: 0
    };
  },
  methods: {
    loadData: function () {
      this.$http
        .get(
          "/dbswitch/admin/api/v1/syslog/list/2/" +
          this.currentPage +
          "/" +
          this.pageSize
        )
        .then(
          res => {
            this.loading = false;
            if (0 === res.data.code) {
              this.currentPage = res.data.pagination.page;
              this.pageSize = res.data.pagination.size;
              this.totalCount = res.data.pagination.total;
              this.lists = res.data.data;
            } else {
              alert("加载数据失败:" + res.data.message);
            }
          },
          error => {
            this.$message({
              showClose: true,
              message: "数据加载错误",
              type: "error"
            });
          }
        );
    },
    boolFormat (row, column) {
      if (row.failed === true) {
        return "是";
      } else {
        return "否";
      }
    },
    handleSizeChange: function (pageSize) {
      this.loading = true;
      this.pageSize = pageSize;
      this.loadData();
    },
    handleCurrentChange: function (currentPage) {
      this.loading = true;
      this.currentPage = currentPage;
      this.loadData();
    },
    handleDetail: function (index, row) {
      this.$message({
        showClose: true,
        message: "查看日志详情" + index + " " + row,
        type: "info"
      });
    },
  },
  created () {
    this.loadData();
  }
};
</script>

<style scoped>
.el-table {
  width: 100%;
  height: 100%;
}
.demo-table-expand {
  font-size: 0;
}
.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}
.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}
</style>