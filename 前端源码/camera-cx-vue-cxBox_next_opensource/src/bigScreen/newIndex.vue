<template>
  <div style="width: 100%; height: 100vh; background: #1A0808">
    <div class="big-screen-box" ref="appRef">
      <!-- 顶部标题及 -->
      <div class="header">
        <img class="logo" src="@/assets/images/logo/yihcode_horizontal_dark.png" alt="logo" />
        <p>{{ $t('bigscreen.newindex.24ft84') }}</p>
        <i class="el-icon-s-unfold" @click="changePath"></i>
      </div>

      <!-- 大屏内容 -->
      <div class="content">
        <div class="content-left">
          <!-- 当日告警情况 -->
          <alarmDay></alarmDay>

          <!-- 告警地图 -->
          <alarmMap></alarmMap>
        </div>
        <div class="content-right">
          <!-- 本月事件告警统计 -->
          <alarmMonth></alarmMonth>

          <!-- 事件处置效率 -->
          <alarmEfficiency></alarmEfficiency>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import drawMixin from '@/utils/drawMixin';

import alarmDay from './components/alarmDay.vue';
import alarmEfficiency from './components/alarmEfficiency.vue';
import alarmMap from './components/alarmMap.vue';
import alarmMonth from './components/alarmMonth.vue';

export default {
  mixins: [drawMixin],
  components: { alarmDay, alarmMonth, alarmEfficiency, alarmMap },
  data() {
    return {};
  },
  mounted() {},
  methods: {
    changePath() {
      this.$router.push('/');
    },
  },
};
</script>

<style lang="scss">
.big-screen-box {
  background-image: url('@/assets/images/bigScreen/background.png');
  background-size: 100% 100%;
  background-repeat: no-repeat;
  position: absolute;
  overflow: hidden;
  width: 1920px;
  height: 1080px;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  transform-origin: left top;
  font-family: Source Han Sans;
  color: #c3c3c5;

  .header {
    width: 100%;
    height: 78px;
    background-image: url('@/assets/images/bigScreen/top.png');
    background-size: contain;
    background-repeat: no-repeat;
    background-position: center center;
    position: relative;
    overflow: hidden;

    .logo {
      position: absolute;
      left: 18px;
      top: 27px;
      width: 146px;
      height: 35px;
    }
    .el-icon-s-unfold {
      position: absolute;
      right: 18px;
      top: 24px;
      color: #E53935;
      font-size: 39px;
      margin-right: 30px;
      margin-top: 4px;
      cursor: pointer;
    }

    p {
      color: #c3c3c5;
      font-size: 32px;
      font-weight: bold;
      line-height: normal;
      letter-spacing: 0.1em;
      text-align: center;
      margin-top: 9px;
    }
  }

  .content {
    padding: 20px;
    display: flex;
    justify-content: space-between;

    .content-left {
      width: 1286px;

      .block-title {
        background-image: url('@/assets/images/bigScreen/card-l.png');
      }
    }

    .content-right {
      width: 574px;

      .block-title {
        background-image: url('@/assets/images/bigScreen/card-r.png');
      }
    }

    .block {
      width: 100%;
      backdrop-filter: blur(20px);
      margin-bottom: 15px;

      .block-title {
        background: linear-gradient(
          to right,
          rgba(24, 53, 181) 0%,
          /* 浅蓝，透明度70% */ rgba(24, 53, 181) 40%,
          /* 浅蓝保持到40%位置 */ rgba(26, 47, 117) 60%,
          /* 中间过渡色 */ rgba(30, 43, 94) 100% /* 深蓝终点 */
        );
        opacity: 1;
        height: 55px;
        background-size: 100% 100%;
        background-repeat: no-repeat;
        padding-left: 20px;
        font-size: 24px;
        font-weight: bold;
        display: flex;
        align-items: center;
        color: #ffffff;
      }
    }
  }
}
</style>
