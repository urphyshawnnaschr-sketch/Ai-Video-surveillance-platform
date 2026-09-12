<template>
  <div class="chart" ref="pireChart"></div>
</template>
<script>
import * as echarts from "echarts";
import tdTheme from "@/common/echart/theme.json"; // 引入默认主题
export default {
  data() {
    return {
      cdata: {
        data: [],
      },
      chart: null,
      chartOptions: {
        legend: {
          orient: "vertical",
          align: "left",
          icon: "circle",
          x: "right", //可设定图例在左、右、居中
          y: "top", //可设定图例在上、下、居中
          left: "80%",
          itemGap: 8,
        },
        toolbox: {
          show: true,
        },
        tooltip: {
          trigger: "item",
          formatter: "{b}: {c} ({d}%)",
        },
        series: [
          {
            name: "Nightingale Chart",
            type: "pie",
            radius: [30, 80],
            center: ["40%", "50%"],
            roseType: "area",
            itemStyle: {
              borderRadius: 8,
            },
            data: [],
            label: {
              formatter: (e) => {
                let newStr = " ";
                const name_len = e.data.name.length; //每个内容名称的长度
                if (name_len > 6) {
                  newStr = e.data.name.slice(0, 6) + "...";
                } else {
                  newStr = e.data.name;
                }
                return newStr;
              },
            },
          },
        ],
      },
    };
  },
  props: {
    pireArr: {
      type: Array,
      default: function () {
        return [];
      },
    },
  },
  mounted() {
    this.cdata = {
      data: this.pireArr,
    };
    echarts.registerTheme("myTheme", tdTheme);
    this.initChart();
    this.updateChartData();
  },
  watch: {
    // 监听 pireArr 的变化
    pireArr: {
      handler(newVal) {
        this.cdata = {
          data: newVal,
        };
        this.updateChartData();
      },
      deep: true,
    },
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$refs.pireChart, tdTheme);
      this.chart.setOption(this.chartOptions);
    },
    updateChartData() {
      this.chartOptions.series[0].data = this.cdata.data;
      this.chart.setOption(this.chartOptions);
    }
  },
};
</script>

<style lang="scss" scoped>
.chart {
  width: 100%;
  height: 241px;
}
</style>
