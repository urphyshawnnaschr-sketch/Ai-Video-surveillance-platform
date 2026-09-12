<template>
    <div :id="id" style="height: 200px;width: 100%"></div>
</template>

<script>
    export default {
        data() {
            return {
                chart: null
            };
        },
        props: {
            id: {
                type: String,
                default: 'echartBox'
            },
            xLabel: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            yList: {
                type: Array,
                default: function() {
                    return [];
                }
            },
        },
        mounted(){
            this.chart = this.$echarts.init(document.getElementById(this.id));
            this.chart.setOption({
                grid: {
                    top: '15%',
                    left: '10%',
                    right: '5%',
                    bottom: '20%',
                },
                xAxis: {
                    type: 'category',
                    boundaryGap: false,
                    data: this.xLabel,
                    axisLabel: {  
                        interval:0,  
                        rotate:40
                    },
                },
                yAxis: {
                    min: 0,
                    splitLine: {
                        show: true,
                        lineStyle: {
                            color: '#ddd'
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
                series: [{
                    type: 'line',
                    smooth: false,
                    // showSymbol: false,
                    data: this.yList,
                    itemStyle: {
                        color: '#2baff0'
                    },
                }]
            })
        },
    };
</script>

<style lang="scss" scoped>
</style>