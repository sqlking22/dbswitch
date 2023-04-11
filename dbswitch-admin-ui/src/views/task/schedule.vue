<template>
  <div>
    <el-card>
      <div class="container">
        <el-card class="box-card">
          <div slot="header"
               class="clearfix">
            <span>任务安排列表</span>
          </div>
          <div class="navsBox">
            <ul>
              <li v-for="(item,index) in pageTaskAssignments"
                  :key="index"
                  @click="handleChooseClick(item.id,index)"
                  :class="{active:index==isActive}">[{{item.id}}]{{item.name}}</li>
            </ul>
            <el-pagination small
                           layout="sizes, prev, pager, next"
                           @current-change="handleLoadPageTaskAssignments"
                           :current-page="currentTaskAssignmentPage"
                           :page-sizes="[10, 15, 20]"
                           @size-change="handleLoadPageTaskAssignmentsSizeChange"
                           :page-size="currentTaskAssignmentPageSize"
                           :total="pageTaskAssignmentsTotalCount">
            </el-pagination>
          </div>
        </el-card>

        <div class="contentBox">
          <el-table :header-cell-style="{background:'#eef1f6',color:'#606266'}"
                    :data="jobTableData"
                    size="small"
                    border>
            <template slot="empty">
              <span>记录为空，或者单击左侧任务列表记录来查看作业调度记录</span>
            </template>
            <el-table-column type="expand">
              <template slot-scope="props">
                <el-form label-position="left"
                         inline
                         class="demo-table-expand">
                  <el-form-item label="执行日志:">
                    <el-button size="small"
                               type="danger"
                               @click="handleShowJobLogs(props.row.jobId)">
                      查看
                    </el-button>
                  </el-form-item>
                </el-form>
              </template>
            </el-table-column>
            <el-table-column property="jobId"
                             label="ID"
                             min-width="10%"></el-table-column>
            <el-table-column property="assignmentId"
                             label="任务ID"
                             min-width="15%"></el-table-column>
            <el-table-column property="scheduleMode"
                             label="调度方式"
                             min-width="15%"></el-table-column>
            <el-table-column property="startTime"
                             label="开始时间"
                             min-width="25%"></el-table-column>
            <el-table-column property="finishTime"
                             label="结束时间"
                             min-width="25%"></el-table-column>
            <el-table-column property="duration"
                             label="持续时长(s)"
                             min-width="20%"></el-table-column>
            <el-table-column property="jobStatus"
                             label="执行状态"
                            min-width="20%"></el-table-column>
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

        <el-dialog title="日志详情"
                   :visible.sync="dialogShowLogVisible"
                   :showClose="false"
                   :before-close="handleClose">
          <el-alert v-if="status===0"
                    title="执行状态：未执行"
                    type="info"
                    center
                    show-icon>
          </el-alert>
          <el-alert v-if="status===1"
                    title="执行状态：执行中"
                    type="success"
                    center
                    show-icon>
          </el-alert>
          <el-alert v-if="status===2"
                    title="执行状态：执行失败"
                    type="error"
                    center
                    show-icon>
          </el-alert>
          <el-alert v-if="status===3"
                    title="执行状态：执行成功"
                    type="success"
                    center
                    show-icon>
          </el-alert>
          <el-alert v-if="status===4"
                    title="执行状态：手动取消"
                    type="warning"
                    center
                    show-icon>
          </el-alert>
          <el-input type="textarea"
                    id="log_textarea_id"
                    class="log_textarea_style"
                    :rows="20"
                    v-model="logContent">
          </el-input>
          <div slot="footer"
               class="dialog-footer">
            <el-button size="small"
                       id="butten_cancel_id"
                       type="danger"
                       v-if="status=='1'"
                       @click="handleCancelJob(jobId)">
              终 止
            </el-button>
            <el-button size="small"
                       type="success"
                       @click="handleCloseLogDialog">关 闭</el-button>
          </div>
        </el-dialog>
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
      totalCount: 0,
      currentTaskAssignmentPage: 1,
      currentTaskAssignmentPageSize: 10,
      pageTaskAssignments: [],
      pageTaskAssignmentsTotalCount: 0,
      taskId: '请选择一个任务安排',
      jobTableData: [],
      jobScheduleTime: '',
      isActive: -1,
      array: [],
      dialogShowLogVisible: false,
      logContent: "",
      jobId: 0,
      baseId: 0,
      status: 0,
      timer: null,
    };
  },
  methods: {
    loadPageTaskAssignments: function () {
      this.$http({
        method: "GET",
        url: "/dbswitch/admin/api/v1/assignment/list/" + this.currentTaskAssignmentPage + "/" + this.currentTaskAssignmentPageSize
      }).then(res => {
        if (0 === res.data.code) {
          this.pageTaskAssignments = res.data.data;
          this.pageTaskAssignmentsTotalCount = res.data.pagination.total;
        } else {
          if (res.data.message) {
            alert("初始化任务安排信息失败:" + res.data.message);
          }
        }
      }
      );
    },
    handleLoadPageTaskAssignments: function (currentPage) {
      this.currentTaskAssignmentPage = currentPage;
      this.loadPageTaskAssignments();
    },
    handleLoadPageTaskAssignmentsSizeChange: function (pageSize) {
      this.currentTaskAssignmentPageSize = pageSize;
      this.loadPageTaskAssignments();
    },
    handleClose: function () { },
    handleSizeChange: function (pageSize) {
      this.loading = true;
      this.pageSize = pageSize;
      this.loadJobsData();
    },
    handleCurrentChange: function (currentPage) {
      this.loading = true;
      this.currentPage = currentPage;
      this.loadJobsData();
    },
    loadJobsData: function () {
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/jobs/list/" + this.currentPage + "/" + this.pageSize + "?id=" + this.taskId
      ).then(res => {
        if (0 === res.data.code) {
          this.currentPage = res.data.pagination.page;
          this.pageSize = res.data.pagination.size;
          this.totalCount = res.data.pagination.total;
          this.jobTableData = res.data.data;
        } else {
          if (res.data.message) {
            alert("查询JOB执行历史纪录失败," + res.data.message);
          }
        }
      });
    },
    handleChooseClick: function (taskId, index) {
      this.isActive = index;
      this.taskId = taskId;
      this.loadJobsData();
    },
    handleCancelJob: function (jobId) {
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/cancel?id=" + jobId
      ).then(res => {
        if (0 === res.data.code) {
          // 禁用取消按钮
          var cancelButton = document.getElementById('butten_cancel_id');
          cancelButton.value = "已取消"
          cancelButton.disabled = true;
          this.$message("停止JOB成功");
          this.loadJobsData();
        } else {
          if (res.data.message) {
            alert("JOB停止失败," + res.data.message);
          }
        }
      });
    },
    handleShowJobLogs: function (jobId) {
      this.dialogShowLogVisible = true
      this.jobId = jobId;
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/logs/tail?id=" + jobId + "&size=500"
      ).then(res => {
        //console.log(res.data)
        if (0 === res.data.code) {
          let lists = res.data.data.logs
          this.status = res.data.data.status;
          this.baseId = res.data.data.maxId;
          this.logContent = lists.join("")
          this.scrollMaxheight();
          if (1 === res.data.data.status) {
            this.timer = setInterval(() => {
              this.timerRefreshLogs();
            }, 1000);// 每隔1s定时刷新
          }
        } else {
          if (res.data.message) {
            alert("加载JOB执行日志失败," + res.data.message);
          }
        }
      });
    },
    timerRefreshLogs: function () {
      //console.log("time run ...");
      this.$http.get(
        "/dbswitch/admin/api/v1/ops/job/logs/next?id=" + this.jobId + "&baseId=" + this.baseId
      ).then(res => {
        if (0 === res.data.code) {
          let lists = res.data.data.logs;
          this.logContent = this.logContent + lists.join("");
          this.baseId = res.data.data.maxId;
          this.status = res.data.data.status;
          this.scrollMaxheight();
          if (1 !== res.data.data.status) {
            // 如果不是运行中，则需要关闭定时器
            clearInterval(this.timer);
            this.timer = null;
          }
        }
      });
    },
    scrollMaxheight: function () {
      this.$nextTick(() => {
        setTimeout(() => {
          const textarea = document.getElementById('log_textarea_id');
          textarea.scrollTop = textarea.scrollHeight;
        }, 13)
      })
    },
    handleCloseLogDialog: function () {
      this.dialogShowLogVisible = false;
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  created () {
    this.loadPageTaskAssignments();
  },
  beforeDestroy () {
    if (this.timer) {
      clearInterval(this.timer);
    }
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
  border-collapse: collapse;
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

.filter {
  margin: 10px;
}

.container {
  display: flex;
  height: 100%;
}

.container > * {
  float: left; /* 水平排列 */
}

.container .el-card {
  width: 50%;
  height: 100%;
  overflow: auto;
}

.container .el-card__header {
  padding: 8px 10px;
  border-bottom: 1px solid #ebeef5;
  box-sizing: border-box;
}

.container .navsBox ul {
  margin: 0;
  padding-left: 10px;
}

.container .navsBox ul li {
  list-style: none;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrop;
  cursor: pointer; /*鼠标悬停变小手*/
  padding: 10px 0;
  border-bottom: 1px solid #e0e0e0;
  width: 100%;
}

.container .navsBox .active {
  background: #bcbcbe6e;
  color: rgb(46, 28, 88);
}

.container .contentBox {
  padding: 10px;
  width: calc(100% - 250px);
}

.log_textarea_style .el-textarea__inner {
  padding: 5px 15px;
  width: 100%;
  color: #505664 !important;
  background-color: #1d1b1f !important;
}
</style>
