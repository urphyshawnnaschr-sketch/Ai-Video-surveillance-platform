<template>
    <div>
        <Chart :cdata="cdata" :show="show"/>
    </div>
</template>

<script>
    import Chart from './chart.vue';
    import request from '@/utils/request.js'

    export default {
        data() {
            return {
                cdata: {
                    className: [],
                    data: []
                }
            };
        },
        props: {
            show: {
                type: Boolean,
                default: false
            }
        },
        components: { Chart },
        mounted() {
            this.getCountData()
        },
        methods: {
            getCountData() {
                request.get('/statistic/countAlgorithm30Day').then(({data}) => {
                    console.log(data)
                    for(let item of data) {
                        this.cdata.className.push(item.name);
                        this.cdata.data.push(item.count);
                    }
                })
            }
        }
    };
</script>

<style lang="scss" scoped>
</style>