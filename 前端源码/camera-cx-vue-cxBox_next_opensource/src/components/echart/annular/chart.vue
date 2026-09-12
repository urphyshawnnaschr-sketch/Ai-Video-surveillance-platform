<template>
    <div>
        <Echart
        v-if="!show"
                :options="options"
                id="earlyWarning"
                height="560px"
                width="100%"
        ></Echart>
        <Echart
        v-else
                :options="options"
                id="earlyWarning"
                height="340px"
                width="100%"
        ></Echart>
    </div>
</template>

<script>
    import Echart from '@/common/echart'

    export default {
        data() {
            return {
                options: {},
                colorList: ['#40d7d9', '#2d70ec', '#f1bd42', '#83aaf5', '#58a55c', '#029dff', '#de67ec', '#ec326d', '#9e84ec', '#8B008B', '#7B68EE', '#0000CD', '#708090', '#7FFFAA', '#FFD700']
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
                default: false,
            }
        },
        methods: {
            getColor(data){
                if(data == 0){
                    return 'rgba(0,0,0,0)'
                }
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
        watch: {
            cdata: {
                handler(newData) {
                    var objData = this.array2obj(newData.data,'name')
                    this.options = {
                        legend: {
                            show: true,
                            icon: "circle",
                            bottom: "0",
                            data: this.getArrayValue(newData.data, "name"),
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
                            data: this.getData(newData.data).yAxis
                        }],
                        xAxis: [{
                            show: false
                        }],
                        series: this.getData(newData.data).series
                    }
                },
                immediate: true,
                deep: true
            }
        }
    };
</script>