<template>
  <div class="tool-wrap">
    <div class="tool-title">{{ $t("annotate.toolbar.658570") }}</div>
    <ul>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.0ot3s7") }}</div>
        <li
          :class="{ disabled: !revokeList.length }"
          @click="setMode('REVOKE')"
        >
          <SvgIcon icon-name="revoke" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.xvk9hs") }}</div>
        <li
          :class="{ disabled: !recoverList.length }"
          @click="setMode('RECOVER')"
        >
          <SvgIcon icon-name="recover" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.1mg887") }}</div>
        <li @click="setMode('ZOOMOUT')"><i class="icon-aios_zoomin1"></i></li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.bh5ci8") }}</div>
        <li @click="setMode('ZOOMIN')"><i class="icon-aios_zoomout1"></i></li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.oym514") }}</div>
        <li @click="setMode('DELETETAG')">
          <i class="icon-aios_clear_label"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("classify.toolbar.22y565") }}</div>
        <li
          :class="{ active: modeType == 'MOVE' }"
          @click="setMode(modeType == 'MOVE' ? '' : 'MOVE')"
        >
          <i class="icon-aios_hand"></i>
        </li>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        placement="right"
        v-if="!tagVisible"
      >
        <div slot="content">{{ $t("classify.toolbar.6h7156") }}</div>
        <li @click="setMode('SHOWTAG')">
          <i class="icon-aios_xianshixinxi"></i>
        </li>
      </el-tooltip>
      <el-tooltip
        class="item"
        effect="dark"
        placement="right"
        v-if="tagVisible"
      >
        <div slot="content">{{ $t("classify.toolbar.2ts26q") }}</div>
        <li @click="setMode('HIDETAG')">
          <i class="icon-aios_yincang"></i>
        </li>
      </el-tooltip>
      <el-popover placement="right" width="400" trigger="click">
        <el-row>
          <el-col :span="4" class="row-col">{{
            $t("annotate.toolbar.7gn11e")
          }}</el-col>
          <el-col :span="14" class="row-col">
            <el-slider
              style="width: 200px"
              :min="0"
              :max="300"
              v-model="imgStype.imgNum1"
              :show-tooltip="false"
            ></el-slider>
          </el-col>
          <el-col :span="6" class="row-col">
            <div class="mo-input--number">
              <el-input-number
                v-model="imgStype.imgNum1"
                controls-position="right"
                :min="0"
                :max="300"
              />
              <div class="define-append">%</div>
            </div>
          </el-col>
          <el-col :span="4" class="row-col">{{
            $t("annotate.toolbar.h0951r")
          }}</el-col>
          <el-col :span="14" class="row-col">
            <el-slider
              style="width: 200px"
              :min="0"
              :max="300"
              v-model="imgStype.imgNum2"
              :show-tooltip="false"
            ></el-slider>
          </el-col>
          <el-col :span="6" class="row-col">
            <div class="mo-input--number">
              <el-input-number
                v-model="imgStype.imgNum2"
                controls-position="right"
                :min="0"
                :max="300"
              />
              <div class="define-append">%</div>
            </div>
          </el-col>
          <el-col :span="4" class="row-col">{{
            $t("annotate.toolbar.p2aykg")
          }}</el-col>
          <el-col :span="14" class="row-col">
            <el-slider
              style="width: 200px"
              :min="0"
              :max="300"
              v-model="imgStype.imgNum3"
              :show-tooltip="false"
            ></el-slider>
          </el-col>
          <el-col :span="6" class="row-col">
            <div class="mo-input--number">
              <el-input-number
                v-model="imgStype.imgNum3"
                controls-position="right"
                :min="0"
                :max="300"
              />
              <div class="define-append">%</div>
            </div>
          </el-col>
          <el-col :span="4" class="row-col">{{
            $t("annotate.toolbar.isg2fy")
          }}</el-col>
          <el-col :span="14" class="row-col">
            <el-slider
              style="width: 200px"
              :min="0"
              :max="100"
              v-model="imgStype.imgNum4"
              :show-tooltip="false"
            ></el-slider>
          </el-col>
          <el-col :span="6" class="row-col">
            <div class="mo-input--number">
              <el-input-number
                v-model="imgStype.imgNum4"
                controls-position="right"
                :min="0"
                :max="300"
              />
              <div class="define-append">%</div>
            </div>
          </el-col>
        </el-row>
        <li slot="reference">
          <SvgIcon icon-name="image" />
        </li>
      </el-popover>
    </ul>
  </div>
</template>

<script>
import SvgIcon from "@/components/svgIcon";
export default {
  components: {
    SvgIcon,
  },
  props: {
    dataList: {
      type: Array,
      default: () => [],
    },
  },
  data() {
    return {
      modeType: "",
      revokeList: [], // 回退列表
      recoverList: [], // 恢复列表
      tagVisible: true,
      allVisible: true,
      imgStype: {
        imgNum1: 100,
        imgNum2: 100,
        imgNum3: 100,
        imgNum4: 0,
      },
    };
  },
  watch: {
    imgStype: {
      immediate: true,
      handler(val) {
        this.$emit("changeImg", val);
      },
      deep: true,
    },
  },
  created() {},
  methods: {
    setMode(val) {
      console.info(val);
      switch (val) {
        case "REVOKE":
          this.modeType = val;
          break;
        case "RECOVER":
          this.modeType = val;
          break;
        case "SHOWTAG":
          this.tagVisible = true;
          this.modeType = val;
          break;
        case "HIDETAG":
          this.tagVisible = false;
          this.modeType = val;
          break;
        case "ZOOMOUT":
          this.modeType = val;
          break;
        case "ZOOMIN":
          this.modeType = val;
          break;
        case "MOVE":
          this.modeType = val;
          break;
        case "DELETETAG":
          this.modeType = val;
          break;
        default:
          this.modeType = "";
          break;
      }
      this.$emit("setMode", this.modeType);
    },
  },
};
</script>
<style scoped lang="scss">
.tool-wrap {
  width: 100px;
  border-right: 1px solid #ebeef5;
  position: relative;
  flex-shrink: 0;
  .tool-title {
    width: 100%;
    height: 40px;
    line-height: 40px;
    font-size: 13px;
    border-bottom: 1px solid #ebeef5;
    padding-left: 20px;
  }
  ul {
    padding: 6px 5.5px;
    display: flex;
    flex-wrap: wrap;
    li {
      width: 40px;
      height: 40px;
      font-size: 18px;
      border-radius: 8px;
      margin: 2px;
      display: flex;
      align-items: center;
      justify-content: center;
      cursor: pointer;
      &:hover {
        color: #E53935;
      }
      &.active {
        color: #E53935;
        background: rgba($color: #E53935, $alpha: 0.1);
      }
      &.disabled {
        color: #bbb;
        background: #e6e8ed;
      }
    }
  }
  .issue {
    width: 40px;
    height: 40px;
    font-size: 18px;
    border-radius: 8px;
    position: absolute;
    bottom: 30px;
    left: 50%;
    margin-left: -20px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    &:hover {
      color: #E53935;
    }
    &.active {
      color: #E53935;
      background: rgba($color: #E53935, $alpha: 0.1);
    }
  }
}
.mo-input--number {
  border: 1px solid #dcdfe6;
  width: 100%;
  display: flex;
  .el-input-number--mini {
    flex: 1;
  }
  ::v-deep .el-input__inner {
    border: none !important;
  }
}

.define-append {
  width: 40px;
  display: inline-block;
  background: #f5f7fa;
  padding: 0px 3px;
  border-left: none;
  height: 32px;
  line-height: 32px;
  color: #1A0808;
  font-size: 12px;
  text-align: center;
}
.row-col {
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: flex-start;
}
::v-deep .el-input {
  margin: 0 !important;
}
::v-deep .el-input-number__increase,
::v-deep .el-input-number__decrease {
  width: 20px !important;
}
::v-deep .el-input-number.is-controls-right .el-input__inner {
  padding-left: 10px;
  padding-right: 25px;
}
</style>
