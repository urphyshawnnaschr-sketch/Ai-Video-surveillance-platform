<template>
    <div class="chart" id="mapChart"></div>
</template>
<script>
import * as echarts from "echarts";
import 'echarts/map/js/china.js'
import tdTheme from '@/common/echart/theme.json' // 引入默认主题
export default {
    data() {
        return {
            cdata: {
                geoCoordMap: {
                    [this.$t('map.index.98dgwi')]: [111.65, 40.82],
                    [this.$t('map.index.7376up')]: [113.65, 34.76],
                    [this.$t('map.index.i94cs7')]: [106.54, 29.59],
                    [this.$t('map.index.e7x54l')]: [102.73, 25.04],
                    [this.$t('map.index.98dgwh')]: [87.68, 43.77],
                },
                data: [
                    { name: this.$t('map.index.98dgwi'), value: 30384 },
                    { name: this.$t('map.index.7376up'), value: 20706 },
                    { name: this.$t('map.index.i94cs7'), value: 86658 },
                    { name: this.$t('map.index.e7x54l'), value: 35258 },
                    { name: this.$t('map.index.98dgwh'), value: 41110 },
                ]
            }
        };
    },

    mounted() {
        echarts.registerTheme('myTheme', tdTheme)
        this.getChart()
    },
    methods: {
        getChart() {
            const chartDom = document.getElementById("mapChart");
            const myChart = echarts.init(chartDom, 'myTheme');
            myChart.clear();
            const option = {
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
                        data: this.convertData(this.cdata.data),
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
            };
            myChart.setOption(option);
        },
        convertData(data) {
            var res = [];
            for (var i = 0; i < data.length; i++) {
                var geoCoord = this.cdata.geoCoordMap[data[i].name];
                console.log(geoCoord, "=============")
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

};
</script>

<style lang="scss" scoped>
.chart {
    width: 340px;
    height: 340px;
}
</style>