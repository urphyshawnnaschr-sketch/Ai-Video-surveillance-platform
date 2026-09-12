<template>
  <div>
    <Chart :cdata="cdata" />
  </div>
</template>

<script>
import Chart from './chart.vue';
export default {
  data () {
    return {
      cdata: [
        {
          // 名字需要与 “common/map/fujian.js” 地图数据文件里面定义的一一对应，不能是 “福州” 或者 “闽” 之类的缩写
          name: this.$t('map.fujian.37jn09'),
          value: 10,
          elseData:{
            // 这里放置地图 tooltip 里想显示的数据
          }
        },
        {
          name: this.$t('map.fujian.hr37b0'),
          value: 9,
        },
        {
          name: this.$t('map.fujian.i5c287'),
          value: 8,
        },
        {
          name: this.$t('map.fujian.hvih76'),
          value: 7,
        },
        {
          name: this.$t('map.fujian.z6k93r'),
          value: 6,
        },
        {
          name: this.$t('map.fujian.26432j'),
          value: 5,
        },
        {
          name: this.$t('map.fujian.cy8zm7'),
          value: 4,
        },
        {
          name: this.$t('map.fujian.8677vk'),
          value: 3,
        },
        {
          name: this.$t('map.fujian.8528u1'),
          value: 2,
        }
      ]
    }
  },
  components: {
    Chart,
  },
  mounted () {
  },
  methods: {
  }
}
</script>

<style lang="scss" scoped>
</style>