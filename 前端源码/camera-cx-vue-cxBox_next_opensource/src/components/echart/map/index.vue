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
        components: { Chart },
        mounted() {
        },
        props: {
            show: {
                type: Boolean,
                default: false
            },
        },
    };
</script>

<style lang="scss" scoped>
</style>