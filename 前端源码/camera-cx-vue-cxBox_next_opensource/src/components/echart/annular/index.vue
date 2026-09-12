<template>
  <div>
    <Chart :cdata="cdata" :show="show" />
  </div>
</template>

<script>
  import Chart from './chart.vue';
  // import request from '@/utils/request.js'

  export default {
    data() {
      return {
        cdata: {
          data: [
            // {
            //     "name": "烟雾火灾识别",
            //     "value": 40
            // },
            // {
            //     "name": "安全带监测",
            //     "value": 20
            // },
            // {
            //     "name": "积水识别",
            //     "value": 0
            // },
            // {
            //     "name": "电频车识别",
            //     "value": 40
            // },
            // {
            //     "name": "站人监测",
            //     "value": 50
            // },
            // {
            //     "name": "抽烟识别",
            //     "value": 0
            // },
            // {
            //     "name": "安全马甲识别",
            //     "value": 60
            // },
            // {
            //     "name": "叉车监测",
            //     "value": 70
            // },
            // {
            //     "name": "接打电话",
            //     "value": 80
            // },
            // {
            //     "name": "口罩识别",
            //     "value": 90
            // },
            // {
            //     "name": "短袖短裤监测",
            //     "value": 0
            // },
            // {
            //     "name": "安全帽识别",
            //     "value": 0
            // },
            // {
            //     "name": "test",
            //     "value": 0
            // },
            // {
            //     "name": "禁入区域",
            //     "value": 0
            // },
            // {
            //     "name": "跌倒识别",
            //     "value": 0
            // }
          ],
        },
      };
    },
    props: {
      dataArr: {
        type: Array,
        default: function () {
          return [];
        },
      },
      show: {
        type: Boolean,
        default: false,
      },
    },
    components: { Chart },
    mounted() {
      // this.getCountData()
      this.cdata.data = this.dataArr;
    },
    methods: {
      // getCountData() {
      //     request.get('/statistic/valueAlgorithm1Day').then(({data}) => {
      //         for(let item of data.data.datas) {
      //             let obj = {
      //                 name: item.name,
      //                 value: item.value
      //             }
      //             this.cdata.data.push(obj);
      //         }
      //     })
      // }
    },
  };
</script>

<style lang="scss" scoped></style>
