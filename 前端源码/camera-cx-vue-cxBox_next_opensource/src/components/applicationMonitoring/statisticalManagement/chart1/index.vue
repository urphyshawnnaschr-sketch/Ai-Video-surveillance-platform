<template>
  <div id="chart1" style="width: 100%; height: 320px"></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  props: {
    chartData: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {};
  },

  mounted() {
    this.initCharts();
  },

  methods: {
    initCharts() {
      const chartDom = document.getElementById("chart1");
      const myChart = echarts.init(chartDom);
      var option = {
        title: {
          text: this.$t('chart1.index.s2k4xk'),
          left: "center",
        },
        tooltip: {
          trigger: "item",
        },
        legend: {
          orient: "vertical",
          left: "left",
        },
        series: [
          {
            name: this.$t('chart1.index.s2k4xk'),
            type: "pie",
            radius: "50%",
            data: this.chartData,
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: "rgba(0, 0, 0, 0.5)",
              },
            },
          },
        ],
      };

      option && myChart.setOption(option);
    },
  },
};
</script>
