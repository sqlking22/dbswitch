<template>
  <div class="dashbord">
    <el-row class="infoCrads">
      <el-col :span="6">
        <div class="cardItem">
          <div class="cardItem_txt">
            <CountTo class="cardItem_p0 color-green1"
                     :startVal="startVal"
                     :endVal="statistics.connectionStatistics.totalCount"
                     :duration="2000"></CountTo>
            <p class="cardItem_p1">数据库连接数</p>
          </div>
          <div class="cardItem_icon">
            <i class="el-icon-s-grid color-green1"></i>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="cardItem">
          <div class="cardItem_txt">
            <CountTo class="cardItem_p0 color-blue"
                     :startVal="startVal"
                     :endVal="statistics.assignmentTaskStatistics.totalCount"
                     :duration="2000"></CountTo>
            <p class="cardItem_p1">任务安排数</p>
          </div>
          <div class="cardItem_icon">
            <i class="el-icon-s-data color-blue"></i>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="cardItem">
          <div class="cardItem_txt">
            <CountTo class="cardItem_p0 color-green2"
                     :startVal="startVal"
                     :endVal="statistics.assignmentTaskStatistics.publishedCount"
                     :duration="2000"></CountTo>
            <p class="cardItem_p1">任务发布数</p>
          </div>
          <div class="cardItem_icon">
            <i class="el-icon-loading color-green2"></i>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="cardItem">
          <div class="cardItem_txt">
            <CountTo class="cardItem_p0 color-red"
                     :startVal="startVal"
                     :endVal="statistics.assignmentJobStatistics.totalCount"
                     :duration="2000"></CountTo>
            <p class="cardItem_p1">作业执行数</p>
          </div>
          <div class="cardItem_icon">
            <i class="el-icon-office-building color-red"></i>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-card class="box-card">
      <div slot="header"
           class="clearfix">
        <el-select v-model="selectDays"
                   @change="selectChanged"
                   placeholder="请选择统计时间">
          <el-option v-for="item in optionDays"
                     :key="item.value"
                     :label="item.label"
                     :value="item.value">
          </el-option>
        </el-select>
        <div id="myChart"></div>
      </div>
    </el-card>
  </div>
</template>
<script>
import CountTo from "vue-count-to";

export default {
  name: "Dashboard",
  components: {
    CountTo
  },
  data () {
    return {
      startVal: 0,
      statistics: {
        connectionStatistics: {
          totalCount: 0
        },
        assignmentTaskStatistics: {
          totalCount: 0,
          publishedCount: 0
        },
        assignmentJobStatistics: {
          totalCount: 0,
          runningCount: 0,
          successfulCount: 0,
          failedCount: 0,
          waitingCount: 0
        }
      },
      optionDays: [
        { label: '1日内', value: 1 },
        { label: '3日内', value: 3 },
        { label: '7日内', value: 7 },
        { label: '30日内', value: 30 },
      ],
      selectDays: 7,
      xAxisData: [],
      y1AxisData: [],
      y2AxisData: [],
      echartOption: {},
      myChart: {},
    };
  },
  methods: {
    loadTotal: function () {
      this.$http
        .get(
          "/dbswitch/admin/api/v1/overview/statistics"
        )
        .then(
          res => {
            if (0 === res.data.code) {
              this.statistics = res.data.data;
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
    loadData: function () {
      this.$http
        .get(
          "/dbswitch/admin/api/v1/overview/trend/" + this.selectDays
        )
        .then(
          res => {
            if (0 === res.data.code) {
              var lists = res.data.data;
              this.xAxisData = [];
              this.y1AxisData = [];
              this.y2AxisData = [];
              for (var i = 0; i < lists.length; i++) {
                this.xAxisData.push(lists[i].dateOfDay);
                this.y1AxisData.push(lists[i].countOfJob);
                this.y2AxisData.push(lists[i].countOfTask);
              }
              this.refreshEchartOption();
              if (this.myChart && typeof this.myChart.setOption == 'function') {
                this.myChart.setOption(this.echartOption, true);
              }
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
    refreshEchartOption: function () {
      this.echartOption = {
        tooltip: {
          trigger: "axis"
        },
        legend: {
          data: [
            {
              name: '作业数',
              textStyle: {
                color: '#000'
              }
            },
            {
              name: '任务数',
              textStyle: {
                color: '#000'
              }
            }
          ]
        },
        grid: {
          left: "3%",
          right: "4%",
          bottom: "3%",
          containLabel: true
        },

        xAxis: {
          type: "category",
          boundaryGap: true,
          data: JSON.parse(JSON.stringify(this.xAxisData)),
          axisLabel: {
            interval: 0,
            textStyle: {
              color: '#000',
              fontSize: 10
            },
            margin: 8
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: 'rgb(2,121,253)'
            }
          },
          axisTick: {
            show: false,
          }
        },
        yAxis: {
          type: "value"
        },

        series: [
          {
            name: "作业数",
            type: "bar",
            barWidth: '8%',
            data: JSON.parse(JSON.stringify(this.y1AxisData))
          },
          {
            name: "任务数",
            type: "bar",
            barWidth: '8%',
            data: JSON.parse(JSON.stringify(this.y2AxisData))
          }
        ]
      };

    },
    initCharts: function () {
      // 基于准备好的dom，初始化echarts实例
      this.myChart = this.$echarts.init(document.getElementById("myChart"));
      this.myChart.setOption(this.echartOption, true);
    },
    selectChanged: function () {
      this.loadData();
    }
  },
  created () {
    this.loadTotal();
    this.loadData();
  },
  mounted () {
    window.addEventListener('resize', () => {
      if (this.myChart && typeof this.myChart.resize == 'function') {
        this.myChart.resize();
      }
    });
  },
  updated () {
    this.refreshEchartOption();
    this.initCharts();
  }
};
</script>

<style scoped>
.dashbord {
  background-color: #f0f3f4;
}

.color-green1 {
  color: #40c9c6 !important;
}
.color-blue {
  color: #36a3f7 !important;
}
.color-red {
  color: #f4516c !important;
}
.color-green2 {
  color: #34bfa3 !important;
}
.dashbord {
  background-color: #f0f3f4;
}

.infoCrads {
  margin: 20px 20px 20px 20px;
}

.infoCrads .el-col {
  padding: 10px 20px;
}

.infoCrads .el-col .cardItem {
  height: 128px;
  background: #fff;
}

.cardItem {
  color: #666;
}

.cardItem .cardItem_txt {
  float: left;
  margin: 26px 0 0 20px;
}

.cardItem .cardItem_txt .cardItem_p0 {
  font-size: 20px;
  margin: 26px 0 0 20px;
}

.cardItem .cardItem_txt .cardItem_p1 {
  font-size: 20px;
  margin: 26px 0 0 20px;
}

.cardItem .cardItem_icon {
  font-size: 64px;
  font-weight: bold;
}

#myChart {
  width: 95%;
  height: 400px;
}
</style>