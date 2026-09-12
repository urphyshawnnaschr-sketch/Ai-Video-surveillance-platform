<template>
  <div>
    <Echart
        v-if="!show"
      :options="options"
      id="earlyWarning"
      height="500px"
      width="500px"
    ></Echart>
    <Echart
    v-else
      :options="options"
      id="earlyWarning"
      height="340px"
      width="340px"
    ></Echart>
  </div>
</template>

<script>
import Echart from '@/common/echart'
import 'echarts/map/js/china.js' 
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
    methods: {
        convertData(data){
            var res = [];
            for (var i = 0; i < data.length; i++) {
                var geoCoord = this.cdata.geoCoordMap[data[i].name];
                console.log(geoCoord)
                if (geoCoord) {
                    res.push({
                        name: data[i].name,
                        value: geoCoord.concat(data[i].value),
                    });
                }
            }
            return res;
        }
    },
  watch: {
    cdata: {
      handler (newData) {
          console.log(newData)
        this.options = {
            // title: {
            //     text: '全国地图',
            //     subtext: 'TOP 20',
            //     left: 'center',
            //     textStyle: {
            //         color: '#fff',
            //     },
            // },
            tooltip: {
                trigger: 'item',
            },
            // legend: {
            //     orient: 'vertical',
            //     bottom: '30',
            //     x: 'center',
            //     data: ['工业氮氧化物'],
            //     textStyle: {
            //         color: '#fff',
            //     },
            // },

            geo: [
                {
                    show: true,
                    map: 'china',
                    label: {
                        normal: {
                            show: false,
                        },
                        emphasis: {
                            show: false,
                        },
                    },
                    roam: false,//地图设置不可拖拽，固定的
                    itemStyle: {
                        normal: {
                            areaColor: '#1D346F',
                            borderWidth: 0,
                            shadowColor: '#D79D3D',
                            shadowBlur: 30,
                            shadowOffsetX: -5,
                            shadowOffsetY: 10,
                        },
                        emphasis: {
                            areaColor: '#2a333d',
                        },
                    },
                },
            ],
            series: [
                {
                    type: 'map',
                    map: 'china',
                    geoIndex: 1,
                    label: {
                        normal: {
                            show: false,
                        },
                        emphasis: {
                            show: false,
                            textStyle: {
                                color: '#fff',
                            },
                        },
                    },
                    roam: false,
                    itemStyle: {
                        normal: {
                            areaColor: '#1D346F',
                            borderColor: '#D79D3D',
                        },
                        emphasis: {
                            areaColor: '#0f2c70',
                        },
                    },
                },
                {
                    name: this.$t('map.chart.5tu93b'),
                    type: 'effectScatter',
                    coordinateSystem: 'geo',
                    data: this.convertData(newData.data),
                    symbolSize: function (val) {
                        return val[2] / 4000;
                    },
                    showEffectOn: 'render',
                    rippleEffect: {
                        brushType: 'stroke',
                    },
                    label: {
                        normal: {
                            formatter: '{b}',
                            position: 'right',
                            fontSize: '12',
                            show: true,
                        },
                    },
                    itemStyle: {
                        normal: {
                            color: '#f4e925',
                            shadowBlur: 10,
                            shadowColor: '#333',
                        },
                    },
                },
            ],
        }
      },
      immediate: true,
      deep: true
    }
  }
};
</script>