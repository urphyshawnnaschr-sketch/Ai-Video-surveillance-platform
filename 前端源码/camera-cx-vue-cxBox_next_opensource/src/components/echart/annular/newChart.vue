<template>
    <div class="chart" id="annularChart"></div>
</template>
<script>
    import * as echarts from "echarts";
    import tdTheme from '@/common/echart/theme.json' // 引入默认主题
    export default {
        data() {
            return {
                cdata: {
                    colorList: ['#40d7d9', '#2d70ec', '#f1bd42', '#83aaf5', '#58a55c', '#029dff', '#de67ec', '#ec326d', '#9e84ec', '#8B008B', '#7B68EE', '#0000CD', '#708090', '#7FFFAA', '#FFD700']
                }
            };
        },
        props: {
            dataArr: {
                type: Array,
                default: function() {
                    return [];
                }
            },
        },
        mounted() {
            echarts.registerTheme('myTheme',tdTheme)
            this.getChart()
        },
        methods:{
            getColor(data){
                if(data == 0){
                    return 'rgba(0,0,0,0)'
                }
            },
            getChart(){
                var objData = this.array2obj(this.dataArr,'name')
                const chartDom = document.getElementById("annularChart");
                const myChart = echarts.init(chartDom,'myTheme');
                myChart.clear();
                const option = {
                    legend: {
                            show: true,
                            icon: "circle",
                            bottom: "0",
                            data: this.getArrayValue(this.dataArr, "name"),
                            formatter: function (name) {
                                return "{title|" + name + "}{value|" + (objData[name].value) + this.$t('annular.chart.4umo5s')
                            },
                            textStyle: {
                                rich: {
                                    title: {
                                        fontSize: 12,
                                        height: 1,
                                        color: "rgb(0, 178, 246)"
                                    },
                                    value: {
                                        fontSize: 12,
                                        color: "#fff"
                                    }
                                }
                            },
                        },
                        tooltip: {
                            show: true,
                            trigger: "item",
                            formatter: "{a}<br>{b}:{c}({d}%)"
                        },
                        color: this.colorList,
                        grid: {
                            top: '-5%',
                            bottom: '70%',
                            left: "50%",
                            containLabel: false
                        },
                        yAxis: [{
                            type: 'category',
                            inverse: true,
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            },
                            axisLabel: {
                                interval: 0,
                                inside: true,
                                textStyle: {
                                    color: "#fff",
                                    fontSize: 13,
                                    lineHeight: '60',
                                    height: 40,
                                    verticalAlign: 'left'
                                },
                                show: true
                            },
                            data: this.getData(this.dataArr).yAxis
                        }],
                        xAxis: [{
                            show: false
                        }],
                        series: this.getData(this.dataArr).series
                };
                myChart.setOption(option);
            },
            getData(data) {
                var res = {
                    series: [],
                    yAxis: [],
                };
                var x = data.length > 8 ? 76 : 73;
                var y = data.length > 8 ? 73 : 68;
                var _index = data.length > 8 ? 5 : 15;
                var arrValue = this.getArrayValue(data, "value");
                var sumValue = eval(arrValue.join('+'));
                for (let i = 0; i < data.length; i++) {
                    res.series.push({
                        name: '',
                        type: 'pie',
                        clockWise: false, //顺时加载
                        hoverAnimation: false, //鼠标移入变大
                        radius: [x - i * _index + '%', y - i * _index + '%'],
                        center: ["50%", "40%"],
                        label: {
                            show: false
                        },
                        itemStyle: {
                            label: {
                                show: false,
                            },
                            labelLine: {
                                show: false
                            },
                            borderWidth: 5,
                        },
                        data: [{
                            value: data[i].value,
                            itemStyle: {
                                color: data[i].value == 0 ? "rgba(0,0,0,0)" : this.colorList[i],
                                borderWidth: 0
                            },
                            name: data[i].name
                        }, {
                            value: sumValue - data[i].value,
                            name: '',
                            itemStyle: {
                                color: "rgba(0,0,0,0)",
                                borderWidth: 0
                            },
                            tooltip: {
                                show: false
                            },
                            hoverAnimation: false
                        }]
                    });
                    res.series.push({
                        name: '',
                        type: 'pie',
                        silent: true,
                        z: 1,
                        clockWise: false, //顺时加载
                        hoverAnimation: false, //鼠标移入变大
                        radius: [x - i * _index + '%', y - i * _index + '%'],
                        center: ["50%", "40%"],
                        label: {
                            show: false
                        },
                        itemStyle: {
                            label: {
                                show: false,
                            },
                            labelLine: {
                                show: false
                            },
                            borderWidth: 5,
                        },
                        data: [{
                            value: 7.5,
                            itemStyle: {
                                color: "rgb(3, 31, 62)",
                                borderWidth: 0
                            },
                            tooltip: {
                                show: false
                            },
                            hoverAnimation: false
                        }, {
                            value: 2.5,
                            name: '',
                            itemStyle: {
                                color: "rgba(0,0,0,0)",
                                borderWidth: 0
                            },
                            tooltip: {
                                show: false
                            },
                            hoverAnimation: false
                        }]
                    });
                    res.yAxis.push(data[i].name);
                }
                return res;
            },
            array2obj(array,key) {
                var resObj = {};
                for(var i=0;i<array.length;i++){
                    resObj[array[i][key]] = array[i];
                }
                return resObj;
            },
            getArrayValue(array, key) {
                var _index = key || "value";
                var res = [];
                if (array) {
                    array.forEach(function(t) {
                        res.push(t[_index]);
                    });
                }
                return res;
            }
        },
    };
</script>

<style lang="scss" scoped>
.chart {
        width: 100%;
        height: 340px;
    }
</style>