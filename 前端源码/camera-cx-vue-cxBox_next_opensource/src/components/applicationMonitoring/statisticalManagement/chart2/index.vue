<template>
  <div id="chart2" style="width: 100%; height: 320px"></div>
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
      const chartDom = document.getElementById("chart2");
      const myChart = echarts.init(chartDom);
      var option = {
        title: {
          text: this.$t('chart2.index.vv63a4'),
        },
        xAxis: {
          type: "category",
          data: this.chartData.xAxiss,
        },
        yAxis: {
          type: "value",
        },
        series: [
          {
            data: this.chartData.values,
            type: "bar",
            showBackground: true,
            backgroundStyle: {
              color: "rgba(180, 180, 180, 0.2)",
            },
          },
        ],
      };

      option && myChart.setOption(option);
    },
  },
};
</script>
