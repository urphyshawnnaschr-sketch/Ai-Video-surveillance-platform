<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue'
export default {
  data () {
    return {
      cdata: {
        category: [
          this.$t('bottomleftchart.index.1j6528'),
          this.$t('bottomleftchart.index.5133r6'),
          this.$t('bottomleftchart.index.1gdv51'),
          this.$t('bottomleftchart.index.51m828'),
          this.$t('bottomleftchart.index.x535d7'),
          this.$t('bottomleftchart.index.rik61s'),
          this.$t('bottomleftchart.index.ovkuu6'),
          this.$t('bottomleftchart.index.690pej'),
          this.$t('bottomleftchart.index.3gb6f4'),
          this.$t('bottomleftchart.index.26el60'),
          this.$t('bottomleftchart.index.15n74z'),
          this.$t('bottomleftchart.index.5k7f4n'),
          this.$t('bottomleftchart.index.9q77g1'),
          this.$t('bottomleftchart.index.nth42s'),
          this.$t('bottomleftchart.index.1vs408'),
          this.$t('bottomleftchart.index.5e72ev'),
          this.$t('bottomleftchart.index.9ea613'),
          this.$t('bottomleftchart.index.4hv9ch'),
          this.$t('bottomleftchart.index.pcj1ge'),
          this.$t('bottomleftchart.index.5x97kq'),
          this.$t('bottomleftchart.index.065nrm'),
          this.$t('bottomleftchart.index.k6to1k'),
          this.$t('bottomleftchart.index.65535d'),
          this.$t('bottomleftchart.index.x63c27'),
          this.$t('bottomleftchart.index.0wrcbk'),
          this.$t('bottomleftchart.index.niy930'),
          this.$t('bottomleftchart.index.twxeyt'),
          this.$t('bottomleftchart.index.658e5t'),
          this.$t('bottomleftchart.index.qi40b5'),
          this.$t('bottomleftchart.index.c52253'),
          this.$t('bottomleftchart.index.qk2225'),
          this.$t('bottomleftchart.index.73u7u7'),
          this.$t('bottomleftchart.index.7b62mt')
        ],
        lineData: [
          18092,
          20728,
          24045,
          28348,
          32808,
          36097,
          39867,
          44715,
          48444,
          50415,
          56061,
          62677,
          59521,
          67560,
          18092,
          20728,
          24045,
          28348,
          32808,
          36097,
          39867,
          44715,
          48444,
          50415,
          36097,
          39867,
          44715,
          48444,
          50415,
          50061,
          32677,
          49521,
          32808
        ],
        barData: [
          4600,
          5000,
          5500,
          6500,
          7500,
          8500,
          9900,
          12500,
          14000,
          21500,
          23200,
          24450,
          25250,
          33300,
          4600,
          5000,
          5500,
          6500,
          7500,
          8500,
          9900,
          22500,
          14000,
          21500,
          8500,
          9900,
          12500,
          14000,
          21500,
          23200,
          24450,
          25250,
          7500
        ],
        rateData: []
      }
    };
  },
  components: {
    Chart,
  },
  mounted () {
    this.setData();
  },
  methods: {
    // 根据自己的业务情况修改
    setData () {
      for (let i = 0; i < this.cdata.barData.length -1; i++) {
        let rate = this.cdata.barData[i] / this.cdata.lineData[i];
        this.cdata.rateData.push(rate.toFixed(2));
      }
    },
  }
};
</script>

<style lang="scss" scoped>
</style>