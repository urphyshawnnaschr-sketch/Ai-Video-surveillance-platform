<template>
    <div>
        <Chart :cdata="cdata" />
    </div>
</template>

<script>
    import Chart from './chart.vue';

    export default {
        data() {
            return {
                cdata: {
                    data: [
                        {value:80,name:this.$t('radius.index.c7tce6'),itemStyle:{color:"#03acd1"}},
                        {value:150,name:this.$t('radius.index.78gp5c'),itemStyle:{color:"#03c781"}},
                        {value:10,name:this.$t('radius.index.cqmp4u'),itemStyle:{color:"#fc2d8a"}},
                        {value:45,name:this.$t('radius.index.nk25b7'),itemStyle:{color:"#96b497"}},
                        {value:150,name:this.$t('radius.index.x8656b'),itemStyle:{color:"#0055ff"}},
                        {value:150,name:this.$t('radius.index.i2v974'),itemStyle:{color:"#ff7708"}},
                        {value:120,name:this.$t('radius.index.qsvp11'),itemStyle:{color:"#fc2d8a"}},
                    ],
                }
            };
        },
        components: { Chart },
        mounted() {
        },
    };
</script>

<style lang="scss" scoped>
</style>