<template>
  <div>
    <div class="chart" id="chart1"></div>
  </div>
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
  watch: {
    chartData: {
      immediate: true,
      deep: true,
      handler(val) {
        this.$nextTick(() => {
          let xAxis = val.map((v) => {
            return v.date;
          });
          let imageNum = val.map((v) => {
            return v.imageNum;
          });
          let annNum = val.map((v) => {
            return v.annNum;
          });
          let seriesData = [
            {
              name: this.$t('annotationplatform.incrementalrraining.v4t8uc'),
              type: "line",
              data: imageNum,
            },
            {
              name: this.$t('chart1.index.urwktt'),
              type: "line",
              data: annNum,
            },
          ];
          const chartDom = document.getElementById("chart1");
          const myChart = echarts.init(chartDom);
          const option = {
            backgroundColor: "#fff",
            tooltip: {
              trigger: "axis",
            },
            legend: {
              data: [this.$t('annotationplatform.incrementalrraining.v4t8uc'), this.$t('chart1.index.urwktt')],
              top: "3%",
              right: "3%",
            },
            grid: {
              top: "15%",
              left: "3%",
              right: "3%",
              bottom: 40,
              containLabel: true,
            },
            xAxis: {
              type: "category",
              boundaryGap: false,
              data: xAxis,
            },
            yAxis: {
              type: "value",
              name: this.$t('projectdetail.overview.7m7byy'),
              min: 10,
              minInterval: 1,
            },
            series: seriesData,
            dataZoom: [
              {
                type: "slider",
                xAxisIndex: 0, //这里是从X轴的0刻度开始
                show: true,
                bottom: 20,
                left: 15,
                right: 15,
                start: 0,
                end: 100,
                minSpan: 0,
                maxSpan: 100,
                height: 10,
                handleSize: "30px",
                fillerColor: "rgba(19, 194, 107, 0.1)",
                borderColor: "rgba(19, 194, 107, 0.3)",
              },
            ],
          };
          myChart.setOption(option);
          window.addEventListener("resize", () => {
            myChart.resize();
          });
        });
      },
    },
  },
  data() {
    return {};
  },
  methods: {},
};
</script>
<style scoped lang="scss">
.chart {
  width: 100%;
  height: 348px;
}
</style>
