<template>
  <div>
    <Echart
      :options="options"
      id="earlyWarning"
      height="450px"
      width="100%"
    ></Echart>
  </div>
</template>

<script>
import Echart from '@/common/echart'
export default {
  data () {
    return {
      options: {},
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
  },
  watch: {
    cdata: {
      handler (newData) {
        this.options = {
            title: [
                {
                    // text: '标题',
                    textStyle: {
                        fontSize: 18,
                        color: "black"
                    },
                    left: "2%"
                },
               ],
           /* tooltip: {
                trigger: 'item',
                formatter:function (parms){
                    var str = parms.marker+""+parms.data.legendname+"</br>"+
                        "Quantity: "+ parms.data.value+"</br>"+
                        "Ratio: "+ parms.percent+"%";
                    return  str ;
                }
            },*/
            legend: {
                bottom: '40',
                icon: "circle",
                left: 'center'
            },
            series: [
                {
                    name:this.$t('radius.chart.56d21e'),
                    type:'pie',
                    center: ['50%', '40%'],
                    radius: ['30%', '55%'],
                    clockwise: false, //饼图的扇区是否是顺时针排布
                    avoidLabelOverlap: false,
                    label: {
                        normal: {
                            show: true,
                            position: 'outter',
                            formatter:function (parms){
                                return parms.data.name + '\n' + parms.data.value;
                            }
                        }
                    },
                    labelLine: {
                        show:false,
                        normal: {
                            length:35,
                            length2:5,
                            smooth:false,
                        }
                    },
                    data:newData.data
                }
            ]
        }
      },
      immediate: true,
      deep: true
    }
  }
};
</script>