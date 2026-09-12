<template>
  <div class="wrap-container sn-container">
    <div class="sn-content">
      <div class="sn-body">
        <div class="wrap-container">
          <div class="table">
            <table
              border="0"
              cellpadding="0"
              cellspacing="0"
              class="table-header"
            >
              <tbody>
                <tr>
                  <td width="20%">
                    <span>{{ $t("components.alarmlist.071p6u") }}</span>
                  </td>
                  <td width="30%">
                    <span>{{ $t("components.alarmlist.hw4u1a") }}</span>
                  </td>
                  <td width="25%">
                    <span>{{ $t("components.alarmlist.4614c5") }}</span>
                  </td>
                  <td width="25%">
                    <span>{{ $t("components.alarmlist.6i2j8d") }}</span>
                  </td>
                </tr>
              </tbody>
            </table>
            <div @click="handleButton($event)">
              <vue-seamless-scroll
                :data="listData"
                class="seamless-warp"
                :class-option="optionSetting"
              >
                <table border="0" cellpadding="0" cellspacing="0" class="item">
                  <tbody>
                    <tr
                      :style="{
                        background: i % 2 == 0 ? '#152959' : '#0e204e',
                      }"
                      v-for="(item, i) in listData"
                      :key="i"
                      class="labelIndex"
                      :id="i"
                    >
                      <td width="20%" class="title">
                        <span>{{ i }}</span>
                      </td>
                      <td width="30%" class="title" style="color: white">
                        <span>{{ item.alarmTime }}</span>
                      </td>
                      <td width="25%" style="color: white">
                        <span>{{ item.cameraName }}</span>
                      </td>
                      <td width="25%" style="color: white">
                        <span>{{ item.algorithmName }}</span>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </vue-seamless-scroll>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import vueSeamlessScroll from "vue-seamless-scroll";
export default {
  name: "seamless-scroll",
  components: {
    vueSeamlessScroll,
  },
  data() {
    return {
      // 数据
      listData: [],
    };
  },
  props: {
    data: {
      type: Array,
      default: () => {
        return [];
      },
    },
  },
  // mounted() {
  //     this.listData = this.data;
  //     console.log('222',this.data)
  // },
  computed: {
    optionSetting() {
      return {
        step: 0.52, // 数值越大速度滚动越快
        limitMoveNum: this.listData.length, // 开始无缝滚动的数据量 this.dataList.length
        hoverStop: true, // 是否开启鼠标悬停
        direction: 1, // 0向下 1向上 2向左 3向右
        autoPlay: true, // 是否自动滚动
        openWatch: true, // 开启数据实时监控刷新dom
        singleHeight: 0, // 单步运动停止的高度(默认值0是无缝不停止的滚动) direction => 0/1
        singleWidth: 0, // 单步运动停止的宽度(默认值0是无缝不停止的滚动) direction => 2/3
        waitTime: 1000, // 单步运动停止的时间(默认值1000ms)
      };
    },
  },
  watch: {
    data(val, oVal) {
      this.listData = val;
    },
  },
  methods: {
    // 处理鼠标点击到哪条，可添加跳转
    handleButton(e) {
      let length = e.path.length;
      let labelIndex = -1;
      for (let i = 0; i < length; i++) {
        if (e.path[i].className === "labelIndex") {
          labelIndex = i;
          break;
        }
      }
      if (labelIndex !== -1) {
        console.log(
          e.path[labelIndex].innerText,
          " =====> e.path[labelIndex].innerText"
        );
        alert(
          "labelIndex.id = " +
            e.path[labelIndex].id +
            ",title： " +
            this.listData[e.path[labelIndex].id].title
        );
      } else {
        alert(this.$t("components.alarmlist.4rg3p9"));
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.wrap-container {
  background: #08173f;
}
.sn-title {
  text-align: center;
}
.sn-container {
  // width: 454px;
  height: 285px;
  %table-style {
    width: 100%;
    height: 35px;
    table-layout: fixed;
    tr {
      td {
        padding: 10px 5px;
        text-align: center;
        background-color: transparent;
        white-space: nowrap;
        overflow: hidden;
        color: #1dc4e8;
        font-size: 13px;
      }
    }
  }
  .table {
    .table-header {
      @extend %table-style;
    }

    .seamless-warp {
      height: 246px;
      overflow: hidden;
      visibility: visible;
      /*background: ;*/
      .colorRed {
        color: #ff4669;
      }
      .colorOrange {
        color: #ffc956;
      }
      .item {
        height: auto;
        @extend %table-style;
        tr {
          td {
            height: 45px;
            &.title {
              text-overflow: ellipsis;
              /*display: inline-block;*/
            }
          }
        }
      }
    }
  }
}
</style>
