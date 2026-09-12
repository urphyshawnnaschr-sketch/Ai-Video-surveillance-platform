<template>
  <div class="alg-detail">
    <div class="top-bg">
      <div @click="goBack()" class="go-back">
        <img src="@/assets/images/modelTesting/back-icon.png" style="width: 20px" />
      </div>
      <div style="display: flex; align-items: flex-start; margin-top: 12px">
        <div style="width: 40%">
          <div class="alg-flex">
            <div class="alg-name">{{ isEnglish() ? params.nameEn : params.name }}</div>
            <div class="tag-flex">
              <div class="tag-item" v-for="(item, index) in tagNameList" :key="index">
                {{ item }}
              </div>
            </div>
          </div>
          <div class="alg-nameEn">
            <span>{{ isEnglish() ? params.nameEn : params.name }}</span>
            <span style="margin-left: 5px"
              >{{ $t('modeltesting.modeldetail.u2vpn8')
              }}{{ $moment(params.updatedAt).format('YYYY-MM-DD') }}</span
            >
          </div>
          <div class="tip-cont">{{ isEnglish() ? params.detail : params.marks }}</div>
        </div>
        <div class="right-txt">
          <div class="base-txt" style="margin-right: 50px">
            <div class="base-precision">
              {{ detailMap.basePrecision ? detailMap.basePrecision : '' }}
            </div>
            <div style="color: #333333; font-size: 16px">
              <span style="padding-right: 5px">{{ $t('modeltesting.modeldetail.qsd6fl') }}</span>
              <span
                class="el-icon-warning-outline"
                @mouseenter="onHover(1)"
                @mouseleave="hideMessage()"
              ></span>
            </div>
            <div class="base-tip" v-if="isBase">
              <div class="tip-title">
                {{ $t('modeltesting.modeldetail.qsd6fl') }}
              </div>
              <div class="tip-txt">
                {{ $t('modeltesting.modeldetail.201bx1') }}
              </div>
            </div>
          </div>
          <div class="scene-txt">
            <div class="scene-precision">
              {{ detailMap.scenePrecision ? detailMap.scenePrecision : '' }}
            </div>
            <div style="color: #333333; font-size: 16px">
              <span style="padding-right: 5px">{{ $t('modeltesting.modeldetail.24zjl7') }}</span>
              <span
                class="el-icon-warning-outline"
                @mouseenter="onHover(2)"
                @mouseleave="hideMessage()"
              ></span>
            </div>
            <div class="base-tip" v-if="isOptimize">
              <div class="tip-title">
                {{ $t('modeltesting.modeldetail.24zjl7') }}
              </div>
              <div class="tip-txt">
                {{ $t('modeltesting.modeldetail.rw21yq') }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- <Upload v-if="params.nameEn" :nameEn="params.nameEn" :id="params.id"></Upload> -->
    <div class="illustrate">
      <div class="headline">
        <!-- <div class="icon"><i class="el-icon-notebook-1"></i></div> -->
        <div class="cont">{{ $t('modeltesting.modeldetail.38i6io') }}</div>
      </div>
      <div class="flex-box" v-if="JSON.stringify(detailMap) != '{}'">
        <div class="effective">
          <div class="title">{{ $t('modeltesting.modeldetail.qruk71') }}</div>
          <div class="demand-tip">
            {{ $t('modeltesting.modeldetail.2115i5') }}
          </div>
          <div class="effective-cont">
            {{ detailMap.trueText }}
          </div>
          <div class="img-flex">
            <div v-for="(item, ind) in detailMap.trueImage" :key="ind" class="img-item">
              <el-image :src="getCoverImageUrl(item)" style="width: 100%; height: 190px"></el-image>
            </div>
          </div>
        </div>
        <div class="invalid">
          <div class="title">{{ $t('modeltesting.modeldetail.37n144') }}</div>
          <div class="effective-cont">
            {{ detailMap.falseText }}
          </div>
          <div class="img-flex">
            <div v-for="(item, ind) in detailMap.falseImage" :key="ind" class="img-item">
              <el-image :src="getCoverImageUrl(item)" style="width: 100%; height: 190px"></el-image>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
  import Cookies from 'js-cookie';

  import { detail, getTagListData } from '@/api/applicationMonitoring/modelTesting';
  import Upload from '@/components/applicationMonitoring/modelTesting/upload.vue';
  import store from '@/store';

  export default {
    data() {
      return {
        params: {},
        detailMap: {},
        tagNameList: [],
        isBase: false,
        isOptimize: false,
        // isShow:true,
        VUE_APP_API_BASE_URL,
        id: '',
        tagList: [],
      };
    },
    created() {
      this.id = this.$route.query.id;
      // 2. 如果路由中没有id，尝试从本地存储恢复（应对动态路由加载延迟的情况）
      if (!this.id) {
        const savedId = sessionStorage.getItem('modelDetailId');
        if (savedId) {
          this.id = savedId;
        }
      } else {
        // 3. 如果获取到id，存入本地存储（刷新后可恢复）
        sessionStorage.setItem('modelDetailId', this.id);
      }
      console.log(this.tagList, '----------');
      this.getTagListData();
      this.getDetail();
    },
    methods: {
      isEnglish() {
        const getLanguage = () => {
          const langMap = {
            'zh-CN': 'zh-CN',
            en: 'en-US',
          };
          return langMap[store.state.locale] || 'zh-CN';
        };
        return getLanguage().startsWith('en');
      },
      // 获取行业列表
      async getTagListData() {
        this.tagList = [];
        const data = await getTagListData({ type: 1 });
        this.tagList = [...data.data];
      },
      async getDetail() {
        const res = await detail({ id: this.id });
        let arr = res.data.tagIds;
        let newArr = [];
        this.tagList.map((item) => {
          arr.map((items) => {
            if (item.id == items) {
              newArr.push(item.name);
            }
          });
        });
        this.tagNameList = newArr;
        this.params = res.data;
        this.detailMap = res.data.detailMap;
      },
      // 返回上一级
      goBack() {
        // this.$emit('close')
        // this.$router.go(-1);
        this.$router.replace('/algorithmManagement/modelTesting');
      },
      // 鼠标移入
      onHover(type) {
        if (type == 1) {
          this.isBase = true;
          this.isOptimize = false;
        } else {
          this.isBase = false;
          this.isOptimize = true;
        }
      },
      // 鼠标移出
      hideMessage() {
        this.isBase = false;
        this.isOptimize = false;
      },
      // modelFun(){
      //     this.isShow=!this.isShow;
      // },
      // 获取图片
      getCoverImageUrl(filename) {
        const token = Cookies.get('X-Token');
        return `${VUE_APP_API_BASE_URL}/algorithm/picStream?file=${filename}&X-Token=${token}`;
      },
    },
    components: {
      Upload,
    },
  };
</script>
<style scoped lang="scss">
  .alg-detail {
    .top-bg {
      border-radius: 8px;
      background: url('@/assets/images/modelTesting/bg1.png') no-repeat center center;
      background-size: cover;
      padding: 26px;
      color: #ffffff;
      .go-back {
        cursor: pointer;
      }
      .alg-flex {
        display: flex;
        align-items: center;
        flex-wrap: wrap;
      }
      .alg-name {
        font-family: youhei;
        font-weight: 400;
        font-size: 30px;
        color: #333333;
      }
      .alg-nameEn {
        font-size: 13px;
        color: #666666;
        margin-top: 8px;
      }
      .tag-flex {
        display: flex;
        flex-wrap: wrap;
      }
      .tag-item {
        color: #eb3a2f;
        background: #ffe6e1;
        font-size: 12px;
        padding: 5px 10px;
        border-radius: 30px;
        margin-left: 16px;
        margin-bottom: 5px;
      }
      .tag-item:nth-child(even) {
        color: #f05924 !important;
        background: #f3c696 !important;
      }
      .tip-cont {
        display: flex;
        // font-size: 22px;
        color: #333333;
        font-size: 13px;
        line-height: 30px;
        margin-top: 15px;
      }
      .right-txt {
        margin-bottom: 62px;
        margin-left: 85px;
        display: flex;
        justify-content: end;
        align-items: end;
      }
      .base-txt,
      .scene-txt {
        position: relative;
        .base-tip {
          width: 240px;
          background: #ffffff;
          padding: 16px;
          border-radius: 6px;
          font-size: 12px;
          position: absolute;
          bottom: 25px;
          right: 0px;
          box-shadow: 0px 1px 3px rgba(0, 0, 0, 0.3);
          .tip-title {
            color: #000;
            margin-bottom: 5px;
            font-weight: bold;
          }
          .tip-txt {
            color: rgba(0, 0, 0, 0.6);
            line-height: 20px;
          }
        }
      }
      .base-precision,
      .scene-precision {
        font-family: youhei;
        font-size: 28px;
        margin-bottom: 39px;
      }
      .base-precision {
        color: #eb3a2f;
      }
      .scene-precision {
        color: #efa04c;
      }
      .button-flex {
        display: flex;
        margin-top: 20px;
        .btn-sty {
          // width: 150px;
          background: #f05924;
          font-size: 13px;
          text-align: center;
          line-height: 22px;
          border-radius: 6px;
          padding: 5px 16px;
          cursor: pointer;
        }
      }
    }

    .illustrate {
      .headline {
        margin: 30px 0px;
        .cont {
          color: #202b3d;
          font-size: 18px;
          font-style: normal;
          font-weight: bold;
        }
      }

      .flex-box {
        display: flex;
        justify-content: space-between;
        background: #fff;
        padding: 23px 29px;
        > div {
          width: 47%;
        }

        .title {
          font-size: 22px;
          color: #202b3d;
          font-size: 13px;
          font-weight: bold;
          display: inline-block;
          margin-bottom: 15px;
        }
        .demand-tip {
          font-size: 13px;
          line-height: 22px;
          color: #999999;
          line-height: 30px;
        }
        .effective-cont {
          font-weight: 500;
          font-size: 13px;
          color: #999999;
          line-height: 25px;
          margin-bottom: 20px;
          white-space: pre-line;
        }
        .img-flex {
          display: grid;
          grid-template-columns: repeat(2, 49%);
          justify-content: space-between;
          .img-item {
            width: 100%;
            margin-bottom: 10px;
          }
        }
      }
    }
  }
</style>
