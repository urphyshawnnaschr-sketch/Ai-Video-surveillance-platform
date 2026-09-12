<template>
  <div class="block">
    <div class="block-title">{{ $t('components.alarmday.3x270o') }}</div>
    <div class="alarm-day">
      <div class="alarm-item">
        <img src="@/assets/images/bigScreen/icon1.png" alt="icon" />
        <div class="alarm-content">
          <div class="number">
            <span>{{ alarmObj.totalCount }}</span
            >{{ $t('components.alarmday.eb47rv') }}
          </div>
          <div class="text">{{ $t('components.alarmday.72utq0') }}</div>
        </div>
      </div>
      <div class="alarm-item">
        <img src="@/assets/images/bigScreen/icon3.png" alt="icon" />
        <div class="alarm-content">
          <div class="number">
            <span>{{ alarmObj.handledCount }}</span
            >{{ $t('components.alarmday.eb47rv') }}
          </div>
          <div class="text">{{ $t('components.alarmday.jv6ycu') }}</div>
        </div>
      </div>
      <div class="alarm-item">
        <PieChart :dataSource="productionLineData" />
      </div>
    </div>
  </div>
</template>

<script>
  import { alarmCount, fetchAlertSituation } from '@/api/bigScreen';

  import PieChart from './PieChart.vue';

  export default {
    components: {
      PieChart, // 这里的键名就是模板中要用的标签名
    },
    data() {
      return {
        alarmObj: {},
        timer: null,
        productionLineData: [],
      };
    },
    created() {
      this.getCount();
      this.getAlertSituation();
      this.timer = setInterval(() => {
        this.getCount();
      }, 10000);
    },
    beforeDestroy() {
      // 关闭定时器
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },
    mounted() {},
    methods: {
      async getCount() {
        const res = await alarmCount();
        this.alarmObj = res.data;
      },
      async getAlertSituation() {
        const res = await fetchAlertSituation();
        this.productionLineData = res.data
          .map((i) => ({
            name: i.name,
            value: i.conut,
          }))
          .sort((a, b) => b.value - a.value)
          .slice(0, 5)
          .filter((i) => i.value);
      },
    },
  };
</script>

<style scoped lang="scss">
  .alarm-day {
    height: 145px;
    display: flex;

    .alarm-item {
      width: 33.33%;
      display: flex;
      justify-content: center;
      align-items: center;
      // background-color:#1A0808;
      background: linear-gradient(
        to top,
        rgba(24, 53, 181, 0.7) 10%,
        rgba(24, 53, 181, 0.7) 40%,
        rgba(26, 47, 117, 0.7) 60%,
        rgba(27, 40, 77, 0.7) 100% /* use 1b284d format */
      );

      img {
        width: 120px;
        height: 120px;
        margin-right: 16px;
      }

      .alarm-content {
        .number {
          font-size: 16px;
          span {
            font-size: 54px;
            font-family: yousheheibiao;
            font-weight: bold;
            margin-right: 8px;
            color: #ffffff;
          }
        }

        .text {
          font-size: 20px;
        }
      }
    }
  }
</style>
