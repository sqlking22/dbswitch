<template>
  <div>
    <el-card>
      <div align="right"
           style="margin:10px 5px;"
           width="65%">
        <el-button type="primary"
                   icon="el-icon-document-add"
                   size="small"
                   @click="handleCreate">添加</el-button>
      </div>
      <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                :data="tableData"
                size="small"
                border>
        <el-table-column prop="id"
                         label="编号"
                         min-width="8%"></el-table-column>
        <el-table-column prop="name"
                         label="名称"
                         show-overflow-tooltip
                         min-width="30%"></el-table-column>
        <el-table-column prop="scheduleMode"
                         label="调度"
                         :formatter="stringFormatSchedule"
                         min-width="8%"></el-table-column>
        <el-table-column prop="isPublished"
                         label="已发布"
                         :formatter="boolFormatPublish"
                         :show-overflow-tooltip="true"
                         min-width="8%"></el-table-column>
        <el-table-column prop="createTime"
                         label="时间"
                         min-width="15%"></el-table-column>
        <el-table-column label="操作"
                         min-width="40%">
          <template slot-scope="scope">
            <el-button size="small"
                       type="success"
                       v-if="scope.row.isPublished===false"
                       @click="handlePublish(scope.$index, scope.row)"><i class="el-icon-timer el-icon--right"></i>发布</el-button>
            <el-button size="small"
                       type="warning"
                       v-if="scope.row.isPublished===true"
                       @click="handleRetireTask(scope.$index, scope.row)"><i class="el-icon-delete-location el-icon--right"></i>下线</el-button>
            <el-button size="small"
                       type="danger"
                       v-if="scope.row.isPublished===true"
                       @click="handleRunTask(scope.$index, scope.row)"><i class="el-icon-video-play el-icon--right"></i>执行</el-button>
            <el-dropdown size="small"
                         split-button
                         type="primary">
              更多
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native.prevent="handleUpdate(scope.$index, scope.row)">修改</el-dropdown-item>
                <el-dropdown-item @click.native.prevent="handleDelete(scope.$index, scope.row)">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </template>
        </el-table-column>
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
    </el-card>
  </div>
</template>

<script>
export default {

  data () {
    return {
      loading: true,
      currentPage: 1,
      pageSize: 10,
      totalCount: 2,
      tableData: [],
    };
  },
  methods: {
    loadData: function () {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/assignment/list/" + this.currentPage + "/" + this.pageSize
      }).then(
        res => {
          if (0 === res.data.code) {
            this.currentPage = res.data.pagination.page;
            this.pageSize = res.data.pagination.size;
            this.totalCount = res.data.pagination.total;
            this.tableData = res.data.data;
          } else {
            alert("加载任务列表失败:" + res.data.message);
          }
        },
        function () {
          console.log("failed");
        }
      );
    },
    boolFormatPublish (row, column) {
      if (row.isPublished === true) {
        return "是";
      } else {
        return "否";
      }
    },
    stringFormatSchedule (row, column) {
      if (row.scheduleMode == "MANUAL") {
        return "手动";
      } else {
        return "系统";
      }
    },
    handleCreate: function () {
      this.$router.push('/task/create')
    },
    handleUpdate: function (index, row) {
      this.$router.push({ path: '/task/update', query: { id: row.id } })
    },
    handleDelete: function (index, row) {
      this.$confirm(
        "此操作将此任务ID=" + row.id + "删除么, 是否继续?",
        "提示",
        {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }
      ).then(() => {
        this.$http.delete(
          "/dbswitch/admin/api/v1/assignment/delete/" + row.id
        ).then(res => {
          if (0 === res.data.code) {
            this.loadData();
          } else {
            if (res.data.message) {
              alert("删除任务失败:" + res.data.message);
            }
          }
        });
      });
    },
    handlePublish: function (index, row) {
      this.$http({
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        url: "/dbswitch/admin/api/v1/assignment/deploy?ids=" + row.id,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message("任务发布成功");
          this.loadData();
        } else {
          if (res.data.message) {
            alert("任务发布失败," + res.data.message);
          }
        }
      });
    },
    handleRunTask: function (index, row) {
      this.$http({
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        url: "/dbswitch/admin/api/v1/assignment/run",
        data: JSON.stringify([row.id])
      }).then(res => {
        if (0 === res.data.code) {
          this.$message("手动启动执行任务成功");
          this.loadData();
        } else {
          if (res.data.message) {
            alert("手动启动执行任务失败," + res.data.message);
          }
        }
      });
    },
    handleRetireTask: function (index, row) {
      this.$http({
        method: "POST",
        headers: {
          'Content-Type': 'application/json'
        },
        url: "/dbswitch/admin/api/v1/assignment/retire?ids=" + row.id,
      }).then(res => {
        if (0 === res.data.code) {
          this.$message("下线任务成功");
          this.loadData();
        } else {
          if (res.data.message) {
            alert("下线任务失败," + res.data.message);
          }
        }
      });
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
  },
};
</script>

<style scoped>
.el-card,
.el-message {
  width: 100%;
  height: 100%;
  overflow: auto;
}

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

.el-input.is-disabled .el-input__inner {
  background-color: #f5f7fa;
  border-color: #e4e7ed;
  color: #c0c4cc;
  cursor: pointer;
}
</style>
