<template>
  <div class="chart" id="lindeChart2"></div>
</template>
<script>
import * as echarts from "echarts";
import tdTheme from '@/common/echart/theme.json' // 引入默认主题
export default {
  data() {
    return {
      cdata: {
        xLabel: [],
        goToSchool:[]
      }
    };
  },
  props: {
    xLabel: {
      type: Array,
      default: function() {
        return [];
      }
    },
    goToSchool: {
      type: Array,
      default: function() {
        return [];
      }
    },
    // show: {
    //     type: Boolean,
    //     default: false
    // },
  },
  watch: {
    // 监听 xLabel 和 goToSchool 的变化
    xLabel: {
      handler(newVal) {
        this.cdata = {
          xLabel: newVal,
          goToSchool: this.cdata.goToSchool
        }
      },
      deep: true
    },
    goToSchool: {
      handler(newVal) {
        this.cdata = {
          xLabel: this.cdata.xLabel,
          goToSchool: newVal
        }
      },
      deep: true
    }
  },
  mounted() {
    this.cdata = {
      xLabel: this.xLabel,
      goToSchool: this.goToSchool
    };
    echarts.registerTheme('myTheme',tdTheme)
    this.getChart()
  },
  methods:{
    getChart(){
      const chartDom = document.getElementById("lindeChart2");
      const myChart = echarts.init(chartDom,'myTheme');
      myChart.clear();
      const option = {
        grid: {
          top: '15%',
          left: '10%',
          right: '10%',
          bottom: '15%',
          // containLabel: true
        },
        xAxis: [{
          type: 'category',
          boundaryGap: true,
          axisLine: { //坐标轴轴线相关设置。数学上的x轴
            show: false,
          },
          axisLabel: { //坐标轴刻度标签的相关设置
            textStyle: {
              color: '#fff',
              fontSize: 12
            },
            formatter: function(data) {
              return data
            }
          },
          splitLine: {
            show: false,
          },
          axisTick: {
            show: false,
          },
          data: this.cdata.xLabel
        }],
        yAxis: [
          {
            min: 0,
            right: '3%',
            splitLine: {
              show: true,
              lineStyle: {
                color: '#233653'
              },
            },
            axisLine: {
              show: false,
            },
            axisLabel: {
              show: true,
            },
            axisTick: {
              show: false,
            },
          }],
        series: [{
          name: this.$t('line.chart.836666'),
          type: 'line',
          symbol: 'circle', // 默认是空心圆（中间是白色的），改成实心圆
          showAllSymbol: true,
          symbolSize:10,
          lineStyle: {
            normal: {
              width: 4,
              color: "#2baff0", // 线条颜色
            },
          },
          itemStyle: {
            color: "#2baff0",
            borderWidth:3,
            borderColor:"#fff"
          },
          label:{
            show:true,
          },
          data: this.cdata.goToSchool
        }]

      };
      myChart.setOption(option);
    }
  }
};
</script>

<style lang="scss" scoped>
.chart {
  width: 550px;
  height: 259px;
}
</style>
