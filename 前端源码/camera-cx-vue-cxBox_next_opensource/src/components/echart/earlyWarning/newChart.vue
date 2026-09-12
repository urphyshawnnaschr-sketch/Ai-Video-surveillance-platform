<template>
  <div class="chart" ref="earlyChart"></div>
</template>
<script>
import * as echarts from "echarts";
import tdTheme from "@/common/echart/theme.json"; // 引入默认主题
import request from "@/utils/request.js";
export default {
  data() {
    return {
      cdata: {
        className: [],
        data: [],
      },
      timer: null,
      chart: null,
      chartOptions: {
        extra: {
          // 新增柱状图渐变配置（默认从蓝到紫）
          barColorGradient: {
            direction: "vertical", // 渐变方向：vertical/horizontal
            colorStops: [
              { offset: 0, color: "#E53935" }, // 底部颜色
              { offset: 1, color: "#7266EA" }, // 顶部颜色
            ],
          },
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            type: "shadow",
            shadowStyle: {
              color: "#FFA980",
              opacity: 0.1,
            },
          },
          confine: true,
          width: "20",
        },
        grid: {
          left: "5%",
          right: "10%",
          bottom: "3%",
          top: "3%",
          height: "auto",
          containLabel: true,
        },
        xAxis: {
          type: "value",
          boundaryGap: [0, 0.01],
          axisTick: {
            // 去除坐标轴上的刻度线
            show: false,
          },
          splitLine: {
            // 控制网格线是否显示
            show: true,
            lineStyle: {
              //  改变样式
              width: "0.2",
              color: "#507b7d", // 修改网格线颜色
            },
          },
          axisLine: {
            // y轴的颜色和宽度
            show: false,
            lineStyle: {
              color: "#cfafff", // y坐标轴的轴线颜色
            },
          },
          axisLabel: {
            // x轴的字体样式
            show: true, //这行代码控制着坐标轴x轴的文字是否显示
            textStyle: {
              fontSize: 15, // x轴字体大小
            },
          },
        },
        yAxis: {
          inverse: true,
          type: "category",
          data: [],
          formatter: function (value) {
            if (value.length > 16) {
              return value.substring(0, 16) + "...";
            } else {
              return value;
            }
          },
          axisTick: {
            // 去除坐标轴上的刻度线
            show: false,
          },
          splitLine: {
            // 控制网格线是否显示
            lineStyle: {
              //  改变样式
              width: "0.2",
              color: "#507b7d", // 修改网格线颜色
            },
          },
          axisLine: {
            // y轴的颜色和宽度
            show: false,
            lineStyle: {
              color: "#ca3a40", // y坐标轴的轴线颜色
            },
          },
          axisLabel: {
            // x轴的字体样式
            show: true, //这行代码控制着坐标轴x轴的文字是否显示
            textStyle: {
              fontSize: 15, // x轴字体大小
            },
            formatter: function (name) {
              if (name.length > 16) {
                return name.substring(0, 16) + "...";
              } else {
                return name;
              }
            },
          },
        },
        series: [
          {
            showBackground: true,
            type: "bar",
            data: [],
            itemStyle: {
              normal: {
                color: (param) => {
                  let colorList = ["#20ddff", "#eeb64d"];
                  let x = "";
                  param.dataIndex % 2 == 0 ? (x = 1) : (x = 2);
                  if (x == "1") {
                    return colorList[0];
                  } else {
                    return colorList[1];
                  }
                },
                label: {
                  show: true,
                  position: "right",
                  formatter: (params) => {
                    for (
                      var i = 0, l = this.chartOptions.series[0].data.length;
                      i < l;
                      i++
                    ) {
                      var val1 = params.value;
                      return val1;
                    }
                  },
                },
              },
            },
            barWidth: 20,
          },
        ],
      },
    };
  },
  mounted() {
    this.getCountData();
    this.initChart();
    this.timer = setInterval(this.getCountData, 60000);
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
      this.timer = null;
    }
  },
  methods: {
    getCountData() {
      request.get("/statistic/countAlgorithm30Day").then(({ data }) => {
        this.cdata.className = [];
        this.cdata.data = [];
        for (let item of data) {
          this.cdata.className.push(item.name);
          this.cdata.data.push(item.count);
        }

        this.updateChartData();
      });
    },
    initChart() {
      this.chart = echarts.init(this.$refs.earlyChart, tdTheme);
      this.chart.setOption(this.chartOptions);
    },
    updateChartData() {
      this.chartOptions.series[0].data = this.cdata.data;
      this.chartOptions.yAxis.data = this.cdata.className;
      this.chart.setOption(this.chartOptions);
    },
  },
};
</script>

<style lang="scss" scoped>
.chart {
  width: 500px;
  height: 326px;
}
</style>
