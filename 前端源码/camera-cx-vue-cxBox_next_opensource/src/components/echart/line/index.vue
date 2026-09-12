<template>
    <div>
        <Chart :cdata="cdata" :show="show"/>
    </div>
</template>

<script>
    import Chart from './chart.vue';

    export default {
        data() {
            return {
                cdata: {
                    xLabel: [],
                    goToSchool: []
                }
            };
        },
        props: {
            xLabel: {
                type: Array,
                default: function() {
                    return [];
                }
            },
            goToSchool: {
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
        components: { Chart },
        mounted() {
            this.cdata.xLabel = this.xLabel;
            this.cdata.goToSchool = this.goToSchool;
        },
    };
</script>

<style lang="scss" scoped>
</style>