<template>
  <div class="tech-pie-chart" ref="chartContainer"></div>
</template>

<script>
  import * as echarts from 'echarts';

  export default {
    name: 'PieChart',
    props: {
      dataSource: {
        type: Array,
        required: true,
        validator(value) {
          return (
            value.length > 0 &&
            value.every((item) => typeof item.name === 'string' && typeof item.value === 'number')
          );
        },
      },
      chartHeight: {
        type: String,
        default: '150px',
      },
    },
    data() {
      return {
        chartInstance: null,
        resizeTimer: null, // 修复防抖定时器未定义的问题
      };
    },
    computed: {
      chartContainer() {
        return this.$refs.chartContainer;
      },
    },
    watch: {
      dataSource: {
        handler(newVal) {
          if (this.chartInstance) {
            this.setChartOption(newVal);
          }
        },
        deep: true,
      },
      chartHeight: {
        handler() {
          if (this.chartInstance) {
            this.chartInstance.resize();
          }
        },
        immediate: true,
      },
    },
    methods: {
      initChart() {
        if (
          !this.chartContainer ||
          !this.chartContainer.offsetWidth ||
          !this.chartContainer.offsetHeight
        ) {
          console.warn('ECharts Container doesn't exist or height is0，Cannot initialize');
          return;
        }

        if (this.chartInstance) {
          this.chartInstance.dispose();
        }

        this.chartInstance = echarts.init(this.chartContainer);
        this.setChartOption(this.dataSource);
        window.addEventListener('resize', this.handleResize);
      },

      setChartOption(data) {
        const option = {
          color: ['#E53935', '#FFA726', '#FFEE58', '#66BB6A', '#26C6DA'],
          tooltip: {
            trigger: 'item',
            backgroundColor: 'rgba(10, 25, 47, 0.8)',
            borderColor: '#FFB300',
            borderWidth: 0,
            textStyle: { color: '#e6e6e6' },
            formatter: function (params) {
              return `
      <div class="tooltip-title">${params.seriesName}</div>
      <div class="tooltip-content">
        <span style="color: ${params.color}; margin-right: 4px;">●</span>
        ${params.name}：${params.value} （${params.percent.toFixed(1)}%）
      </div>
    `;
            },
          },
          legend: {
            show: true,
            top: 'center', // 垂直方向居中
            orient: 'vertical',
            right: 20,
            textStyle: {
              color: '#fff',
            },
          },
          series: [
            {
              name: $t('components.alarmday.pieName', { count: data.length }),
              type: 'pie',
              radius: ['45%', '75%'],
              center: ['50%', '50%'],
              avoidLabelOverlap: true,
              itemStyle: {
                borderColor: 'rgba(10, 25, 47, 0.8)',
                borderWidth: 0,
                shadowBlur: 10,
                shadowColor: 'rgba(0, 242, 254, 0.5)',
              },
              emphasis: {
                itemStyle: {
                  shadowBlur: 15,
                  shadowColor: 'rgba(0, 242, 254, 0.8)',
                  scale: 1,
                },
              },

              label: {
                show: false,
                position: 'outside',
                align: 'center',
                verticalAlign: 'middle',
                fontSize: 12,
                color: '#e6e6e6',
                formatter: '{b}', // 只显示名称（若要显示数值可改为 '{b}: {c}'）
              },
              labelLine: {
                show: true,
                length: 0,
                length2: 0,
                lineStyle: {
                  color: '#FFB300',
                  width: 1,
                },
              },
              data: data,
            },
          ],
        };

        this.chartInstance.setOption(option);
      },

      handleResize() {
        if (this.resizeTimer) clearTimeout(this.resizeTimer);
        this.resizeTimer = setTimeout(() => {
          if (this.chartInstance) {
            this.chartInstance.resize();
          }
        }, 100);
      },
    },
    mounted() {
      this.$nextTick(() => {
        this.initChart();
      });
    },
    beforeDestroy() {
      window.removeEventListener('resize', this.handleResize);
      if (this.chartInstance) {
        this.chartInstance.dispose();
        this.chartInstance = null;
      }
    },
    activated() {
      this.$nextTick(() => {
        this.initChart();
      });
    },
  };
</script>

<style scoped>
  .tech-pie-chart {
    width: 500px;
    height: 160px;
    background: transparent;
  }
</style>
