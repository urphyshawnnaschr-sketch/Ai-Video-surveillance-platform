<template>
  <div class="chart" ref="lindeChart"></div>
</template>
<script>
import * as echarts from "echarts";
import tdTheme from "@/common/echart/theme.json"; // 引入默认主题
export default {
  data() {
    return {
      cdata: {
        xLabel: [],
        goToSchool: [],
      },
      chart: null,
      chartOptions: {
        extra: {
          smooth: true,
        },
        grid: {
          top: "15%",
          left: "10%",
          right: "10%",
          bottom: "15%",
          // containLabel: true
        },
        xAxis: [
          {
            type: "category",
            boundaryGap: true,
            axisLine: {
              //坐标轴轴线相关设置。数学上的x轴
              show: false,
            },
            axisLabel: {
              //坐标轴刻度标签的相关设置
              textStyle: {
                color: "#fff",
                fontSize: 12,
              },
              formatter: function (data) {
                return data;
              },
            },
            splitLine: {
              show: false,
            },
            axisTick: {
              show: false,
            },
            data: [],
          },
        ],
        yAxis: [
          {
            min: 0,
            right: "3%",
            splitLine: {
              show: true,
              lineStyle: {
                color: "#233653",
              },
            },
            axisLine: {
              show: false,
            },
            axisLabel: {
              color: '#ffffff',
              show: true,
            },
            axisTick: {
              show: false,
            },
          },
        ],
        series: [
          {
            name: this.$t('line.chart.836666'),
            type: "line",
            symbol: "circle", // 默认是空心圆（中间是白色的），改成实心圆
            showAllSymbol: true,
            smooth: true,
            symbolSize: 10,
            lineStyle: {
              normal: {
                width: 4,
                color: "#2baff0", // 线条颜色
              },
            },
            itemStyle: {
              color: "#2baff0",
              borderWidth: 3,
              borderColor: "#fff",
            },
            label: {
              show: true,
            },
            data: [],
          },
        ],
      },
    };
  },
  props: {
    xLabel: {
      type: Array,
      default: function () {
        return [];
      },
    },
    goToSchool: {
      type: Array,
      default: function () {
        return [];
      },
    },
  },
  mounted() {
    this.cdata = {
      xLabel: this.xLabel,
      goToSchool: this.goToSchool,
    };
    echarts.registerTheme("myTheme", tdTheme);
    this.initChart();
    this.updateChartData();
  },
  watch: {
    // 监听 xLabel 和 goToSchool 的变化
    xLabel: {
      handler(newVal) {
        this.cdata = {
          xLabel: newVal,
          goToSchool: this.cdata.goToSchool,
        };
        this.updateChartData();
      },
      deep: true,
    },
    goToSchool: {
      handler(newVal) {
        this.cdata = {
          xLabel: this.cdata.xLabel,
          goToSchool: newVal,
        };

        this.updateChartData();
      },
      deep: true,
    },
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.lindeChart, tdTheme);
      this.chart.setOption(this.chartOptions);
    },
    updateChartData() {
      this.chartOptions.series[0].data = this.cdata.goToSchool;
      this.chartOptions.xAxis[0].data = this.cdata.xLabel;
      this.chart.setOption(this.chartOptions);
    },
  },
};
</script>

<style lang="scss" scoped>
.chart {
  width: 550px;
  height: 245px;
}
</style>
