<template>
    <div>
        <Chart :cdata="cdata" :show="show" />
    </div>
</template>

<script>
    import Chart from './chart.vue';

    export default {
        data() {
            return {
                cdata: {
                    data: [],
                }
            };
        },
        components: { Chart },
        props: {
            pireArr: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            show: {
                type: Boolean,
                default: false
            },
        },
        mounted() {
            this.cdata.data = this.pireArr;
        },
        watch: {
            // 监听 pireArr 的变化
            pireArr: {
                handler(newVal) {
                    console.log('update pire data')
                    this.cdata = {
                        data: newVal,
                    }
                },
                deep: true
            },
        },
    };
</script>

<style lang="scss" scoped>
</style>