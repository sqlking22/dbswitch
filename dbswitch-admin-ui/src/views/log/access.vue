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
      <el-table-column prop="createTime"
                       label="日志时间"
                       min-width="15%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="ipAddress"
                       label="请求IP"
                       min-width="10%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="content"
                       label="操作内容"
                       min-width="20%"
                       :show-overflow-tooltip="true"></el-table-column>
      <el-table-column prop="userAgent"
                       label="请求代理"
                       min-width="50%"
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
          "/dbswitch/admin/api/v1/syslog/list/1/" +
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

    handleSizeChange: function (pageSize) {
      this.loading = true;
      this.pageSize = pageSize;
      this.loadData();
    },

    handleCurrentChange: function (currentPage) {
      this.loading = true;
      this.currentPage = currentPage;
      this.loadData();
    }
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
</style>