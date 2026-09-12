<template>
  <div class="block">
    <div class="block-title">{{ $t('components.alarmefficiency.fc4pwp') }}</div>
    <div style="padding-right: 20px">
      <div style="display: flex">
        <div class="chart" ref="chartContainerOne"></div>
        <div class="chart" ref="chartContainerTwo"></div>
      </div>
      <div class="chart" ref="chartContainer"></div>
    </div>
  </div>
</template>

<script>
  import * as echarts from 'echarts';

  import { alarm3month, alarm4week, alarm7day } from '@/api/bigScreen';
  import tdTheme from '@/common/echart/theme.json'; // 引入默认主题

  export default {
    data() {
      return {
        timer: null,
        weekList: [],
        monthList: [],
        dayList: [],
        maxNum: null,
      };
    },
    mounted() {
      this.getData();
      this.timer = setInterval(() => {
        this.getData();
      }, 10000);
    },
    beforeDestroy() {
      // 关闭定时器
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    methods: {
      async getData() {
        const res = await alarm4week();
        const month = await alarm3month();
        const day = await alarm7day();
        this.weekList = res.data;
        this.monthList = month.data;
        this.dayList = day.data;
        const newArr = [];
        if (this.weekList && this.weekList.length > 0) {
          this.weekList.forEach((item) => {
            newArr.push(item.timeVal, item.targetVal);
          });
        }
        if (this.monthList && this.monthList.length > 0) {
          this.monthList.forEach((item) => {
            newArr.push(item.timeVal, item.targetVal);
          });
        }
        if (this.dayList && this.dayList.length > 0) {
          this.dayList.forEach((item) => {
            newArr.push(item.timeVal, item.targetVal);
          });
        }
        this.maxNum = newArr.length > 0 ? Math.max(...newArr) : 0;
        this.get3month();
        this.get4week();
        this.get7day();
      },
      get3month() {
        const chartOne = echarts.init(this.$refs.chartContainerOne, tdTheme);
        // chartOne.clear();

        const xAxisList = [];
        const timeList = [];
        const targetList = [];
        if (this.monthList && this.monthList.length > 0) {
          this.monthList.forEach((item) => {
            xAxisList.push(item.date);
            timeList.push(item.timeVal);
            targetList.push(item.targetVal);
          });
        }
        const option = {
          tooltip: {
            trigger: 'axis',
          },
          legend: {
            left: 'right',
            data: [
              this.$t('components.alarmefficiency.4817n7'),
              this.$t('components.alarmefficiency.8a7jh3'),
            ],
            show: false,
          },
          grid: {
            left: '3%',
            right: '0',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'category',
            boundaryGap: [0.1, 0.1],
            data: xAxisList,
          },
          yAxis: {
            type: 'value',
            name: this.$t('components.alarmefficiency.second'), //单位
            max: 600,
            splitLine: {
              // 设置x轴的分割线（网格线）
              show: true, // 显示分割线
              lineStyle: {
                color: '#ccc', // 分割线颜色
                type: 'dashed', // 分割线类型，这里设置为虚线或实线等
                opacity: 0.5, // 设置透明度，范围从0到1，0完全透明，1完全不透明。可以根据需要调整。
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
          },
          series: [
            {
              name: this.$t('components.alarmefficiency.4817n7'),
              type: 'line',
              data: timeList,
              itemStyle: {
                color: '#FFA747', // 保持主色调不变
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(255, 167, 71, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(255, 180, 100, 0.5)', // 中间过渡色（稍亮且更透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(255, 167, 71, 0.1)', // 终点色（主色极浅透明）
                  },
                ]),
              },
            },
            {
              name: this.$t('components.alarmefficiency.8a7jh3'),
              type: 'line',
              data: targetList,
              itemStyle: {
                color: '#FFA747', // 保持主色调（亮青色）不变
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(31, 190, 215, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(71, 205, 223, 0.5)', // 中间过渡色（稍浅且透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(31, 190, 215, 0.1)', // 终点色（主色极浅透明）
                  },
                ]),
              },
            },
          ],
        };
        chartOne.setOption(option);
      },
      get4week() {
        const chartTwo = echarts.init(this.$refs.chartContainerTwo, tdTheme);
        // chartTwo.clear();
        const xAxisList = [];
        const timeList = [];
        const targetList = [];
        if (this.weekList && this.weekList.length > 0) {
          this.weekList.forEach((item) => {
            xAxisList.push(item.date);
            timeList.push(item.timeVal);
            targetList.push(item.targetVal);
          });
        }
        const option = {
          tooltip: {
            trigger: 'axis',
          },
          legend: {
            left: 'right',
            data: [
              this.$t('components.alarmefficiency.4817n7'),
              this.$t('components.alarmefficiency.8a7jh3'),
            ],
            itemHeight: 0,
            itemWidth: 10,
          },
          grid: {
            left: '0',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'category',
            boundaryGap: [0.1, 0.1],
            data: xAxisList,
          },
          yAxis: {
            type: 'value',
            max: 600,
            min: 0,
            splitLine: {
              // 设置x轴的分割线（网格线）
              show: true, // 显示分割线
              lineStyle: {
                color: '#ccc', // 分割线颜色
                type: 'dashed', // 分割线类型，这里设置为虚线或实线等
                opacity: 0.5, // 设置透明度，范围从0到1，0完全透明，1完全不透明。可以根据需要调整。
              },
            },
            axisLine: {
              show: false,
            },
            axisLabel: {
              show: false,
            },
            axisTick: {
              show: false,
            },
          },
          series: [
            {
              name: this.$t('components.alarmefficiency.4817n7'),
              type: 'line',
              data: timeList,
              itemStyle: {
                color: '#FFA747', // 保持主色调不变
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(255, 167, 71, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(255, 180, 100, 0.5)', // 中间过渡色（稍亮且更透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(255, 167, 71, 0.1)', // 终点色（主色极浅透明）
                  },
                ]),
              },
            },
            {
              name: this.$t('components.alarmefficiency.8a7jh3'),
              type: 'line',
              data: targetList,
              itemStyle: {
                color: '#FFA747',
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(31, 190, 215, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(71, 205, 223, 0.5)', // 中间过渡色（稍浅且透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(31, 190, 215, 0.1)', // 终点色（主色极浅透明）
                  },
                ]),
              },
            },
          ],
        };
        chartTwo.setOption(option);
      },
      get7day() {
        const chart = echarts.init(this.$refs.chartContainer, tdTheme);
        // chart.clear();
        const xAxisList = [];
        const timeList = [];
        const targetList = [];
        if (this.dayList && this.dayList.length > 0) {
          this.dayList.forEach((item) => {
            xAxisList.push(item.date);
            timeList.push(item.timeVal);
            targetList.push(item.targetVal);
          });
        }
        const option = {
          tooltip: {
            trigger: 'axis',
          },
          legend: {
            left: 'right',
            data: [
              this.$t('components.alarmefficiency.4817n7'),
              this.$t('components.alarmefficiency.8a7jh3'),
            ],
            show: false,
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true,
          },
          xAxis: {
            type: 'category',
            boundaryGap: [0.1, 0.1],
            data: xAxisList,
          },
          yAxis: {
            name: this.$t('components.alarmefficiency.second'),
            type: 'value',
            max: this.maxNum,
            max: 600,
            min: 0,
            splitLine: {
              // 设置x轴的分割线（网格线）
              show: true, // 显示分割线
              lineStyle: {
                color: '#ccc', // 分割线颜色
                type: 'dashed', // 分割线类型，这里设置为虚线或实线等
                opacity: 0.5, // 设置透明度，范围从0到1，0完全透明，1完全不透明。可以根据需要调整。
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
          },
          series: [
            {
              name: this.$t('components.alarmefficiency.4817n7'),
              type: 'line',
              data: timeList,
              itemStyle: {
                color: '#FFA747', // 保持主色调不变
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(255, 167, 71, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(255, 180, 100, 0.5)', // 中间过渡色（稍亮且更透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(255, 167, 71, 0.1)', // 终点色（主色极浅透明）
                  },
                ]),
              },
            },
            {
              name: this.$t('components.alarmefficiency.8a7jh3'),
              type: 'line',
              data: targetList,
              itemStyle: {
                color: '#FFA747', // 保持主色调（亮青色）不变
              },
              areaStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  {
                    offset: 0,
                    color: 'rgba(31, 190, 215, 0.8)', // 主色#FFA747带80%不透明度
                  },
                  {
                    offset: 0.5,
                    color: 'rgba(71, 205, 223, 0.5)', // 中间过渡色（稍浅且透明）
                  },
                  {
                    offset: 1,
                    color: 'rgba(31, 190, 215, 0.1)', // 终点色（主色极浅透明）
                  },
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
    height: 200px;
    margin-top: 16px;
    // background: #1A0808;
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
