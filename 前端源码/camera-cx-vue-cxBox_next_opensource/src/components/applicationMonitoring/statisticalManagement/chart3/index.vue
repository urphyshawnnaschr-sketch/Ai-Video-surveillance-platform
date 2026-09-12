<template>
  <div id="chart3" style="width: 100%; height: 320px"></div>
</template>

<script>
import * as echarts from "echarts";

export default {
  props: {
    chartData: {
      type: Object,
      default: () => {},
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
      const chartDom = document.getElementById("chart3");
      const myChart = echarts.init(chartDom);
      var option = {
        title: {
          text: this.$t('chart3.index.42fx1r'),
        },
        tooltip: {
          trigger: "axis",
          axisPointer: {
            // type: 'shadow'
          },
        },
        legend: {
          data: this.chartData.algorithmNames,
          align: "right",
          right: 20,
          left: 180,
        },
        toolbox: {
          show: true,
          orient: "vertical",
          left: "right",
          top: "center",
        },
        xAxis: [
          {
            type: "category",
            axisTick: { show: false },
            data: this.chartData.cameraNames,
          },
        ],
        yAxis: [
          {
            type: "value",
          },
        ],
        series: this.chartData.datas,
      };

      option && myChart.setOption(option);
    },
  },
};
</script>
