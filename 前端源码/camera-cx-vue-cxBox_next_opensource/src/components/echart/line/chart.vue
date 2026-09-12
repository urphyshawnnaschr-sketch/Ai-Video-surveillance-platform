<template>
  <div>
    <Echart
    v-if="!show"
      :options="options"
      id="earlyWarning"
      :height="'316px'"
      :width="'657px'"
    ></Echart>
    <Echart
    v-else
      :options="options"
      id="earlyWarning"
      :height="'220px'"
      :width="'550px'"
    ></Echart>
  </div>
</template>

<script>
import Echart from '@/common/echart'
export default {
  data () {
    return {
      options: {},
    };
  },
  components: {
    Echart,
  },
  props: {
    cdata: {
      type: Object,
      default: () => ({})
    },
    show: {
        type: Boolean,
        default: false
    },
  },
  watch: {
    cdata: {
      handler (newData) {
        this.options = {
            grid: {
                top: '15%',
                left: '10%',
                right: '10%',
                bottom: '15%',
                // containLabel: true
            },
            xAxis: [{
                type: 'category',
                boundaryGap: false,
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
                data: newData.xLabel
            }],
            yAxis: [{
                min: 0,
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
                data: newData.goToSchool
            }]
        }
      },
      immediate: true,
      deep: true
    }
  }
};
</script>