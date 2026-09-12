<template>
  <div>
    <Echart
    v-if="!show"
      :options="options"
      id="earlyWarning"
      height="450px"
      width="100%"
    ></Echart>
    <Echart
    v-else
      :options="options"
      id="earlyWarning"
      height="230px"
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
    show: {
      type: Boolean,
      default: false
    },
  },
  watch: {
    cdata: {
      handler (newData) {
        this.options = {
            legend: {
                orient: 'vertical',
                align: "left",
                icon: "circle",
                x: 'right',      //可设定图例在左、右、居中
                y: 'top',     //可设定图例在上、下、居中
                left:'65%',
                itemGap: 8,
            },
            toolbox: {
                show: true,
            },
            series: [
                {
                    name: 'Nightingale Chart',
                    type: 'pie',
                    radius: [30, 80],
                    center: ['30%', '50%'],
                    roseType: 'area',
                    itemStyle: {
                        borderRadius: 8
                    },
                    data: newData.data,
                    label: {
                    formatter: (e) => {
                        let newStr = " "
                        const name_len = e.data.name.length //每个内容名称的长度
                        if(name_len>6){
                          newStr = e.data.name.slice(0, 6) + '...'
                        }else{
                          newStr = e.data.name
                        }
                        return newStr 
                    },
                  }
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