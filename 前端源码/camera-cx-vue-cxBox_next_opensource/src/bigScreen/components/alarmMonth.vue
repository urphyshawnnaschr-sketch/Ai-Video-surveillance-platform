<template>
  <div class="block">
    <div class="block-title">{{ $t('components.alarmmonth.h9p517') }}</div>
    <div class="chart" ref="chartContainer"></div>
  </div>
</template>

<script>
import * as echarts from 'echarts';

import { algorithmAudit } from '@/api/bigScreen';
import tdTheme from '@/common/echart/theme.json'; // 引入默认主题

export default {
  mounted() {
    this.initChart();
  },
  methods: {
    async initChart() {
      const chart = echarts.init(this.$refs.chartContainer, tdTheme);
      const res = await algorithmAudit();
      const xAxisList = [];
      const unhandleList = [];
      const handleList = [];
      if (res.data && res.data.length > 0) {
        res.data.forEach((item) => {
          xAxisList.push(item.algorithmName);
          unhandleList.push(item.unhandleCount);
          handleList.push(item.handledCount);
        });
      }
      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow',
          },
        },
        legend: {
          left: 'right',
          data: [this.$t('components.alarmmonth.x3182j'), this.$t('components.alarmmonth.4qa9k9')],
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            rotate: 45, // 设置标签旋转45度
            interval: 0, // 确保所有标签都显示，即使它们重叠
            textStyle: {
              color: '#ffffff', // 设置标签文字颜色
            },
          },
          data: xAxisList,
        },
        yAxis: {
          type: 'value',
        },
        series: [
          {
            name: this.$t('components.alarmmonth.x3182j'),
            type: 'bar',
            stack: 'total',
            data: handleList,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                // 水平方向渐变（左到右）
                { offset: 0, color: 'rgba(173, 216, 255, 0.8)' }, // 左侧浅蓝色（带透明感，模拟发光）
                { offset: 0.4, color: 'rgba(44, 112, 255, 0.9)' }, // 过渡到中间高级蓝
                { offset: 0.6, color: 'rgba(30, 88, 217, 1)' }, // 中间核心高级蓝（最深色）
                { offset: 1, color: 'rgba(173, 216, 255, 0.8)' }, // 右侧浅蓝色（与左侧对称发光）
              ]),
            },
          },
          {
            name: this.$t('components.alarmmonth.4qa9k9'),
            type: 'bar',
            stack: 'total',
            data: unhandleList,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 1, 0, [
                // 水平方向渐变（左到右）
                { offset: 0, color: 'rgba(255, 230, 180, 0.8)' }, // 左侧浅橙色（带透明感，模拟发光）
                { offset: 0.4, color: 'rgba(255, 180, 80, 0.9)' }, // 过渡到中间深橙色
                { offset: 0.6, color: 'rgba(255, 167, 71, 1)' }, // 中间核心色（#FFA747，最深色）
                { offset: 1, color: 'rgba(255, 230, 180, 0.8)' }, // 右侧浅橙色（与左侧对称发光）
              ]),
            },
          },
        ],
      };
      chart.setOption(option);
    },
  },
};
</script>

<style scoped lang="scss">
.chart {
  width: 100%;
  height: 400px;
  margin-top: 10px;
  // 图表背景色
  background: linear-gradient(
    to bottom,
    rgba(24, 53, 181, 0.7) 0%,
    rgba(24, 53, 181, 0.7) 40%,
    rgba(26, 47, 117, 0.7) 60%,
    rgba(27, 40, 77, 0.7) 100% /* use 1b284d format */
  );
  opacity: 0.8;
}
</style>
