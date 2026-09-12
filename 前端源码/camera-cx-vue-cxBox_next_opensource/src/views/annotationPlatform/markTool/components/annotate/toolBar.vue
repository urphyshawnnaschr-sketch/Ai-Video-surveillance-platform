<template>
  <div class="tool-wrap">
    <div class="tool-title">{{ $t("annotate.toolbar.658570") }}</div>
    <ul>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.3xn901") }}</div>
        <li :class="{ active: modeType == '' }" @click="setMode('')">
          <i class="el-icon-position" style="transform: rotateY(180deg)"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.bs6het") }}<br />{{
            $t("annotate.toolbar.6tf93l")
          }}
        </div>
        <li :class="{ active: modeType == 'RECT' }" @click="setMode('RECT')">
          <i class="icon-aios_continue"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.75h8bk") }}<br />{{
            $t("annotate.toolbar.628m26")
          }}
        </div>
        <li
          :class="{ active: modeType == 'POLYGON' }"
          @click="setMode('POLYGON')"
        >
          <SvgIcon icon-name="polyline" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.3538k7") }}<br />{{
            $t("annotate.toolbar.4y54js")
          }}
        </div>
        <li :class="{ active: modeType == 'POINT' }" @click="setMode('POINT')">
          <SvgIcon icon-name="point" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.o7sb3x") }}</div>
        <li
          :class="{ disabled: !revokeList.length }"
          @click="setMode('REVOKE')"
        >
          <SvgIcon icon-name="revoke" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.c8hrx7") }}</div>
        <li
          :class="{ disabled: !recoverList.length }"
          @click="setMode('RECOVER')"
        >
          <SvgIcon icon-name="recover" />
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content" v-if="tagVisible">
          {{ $t("annotate.toolbar.8s438i") }}<br />{{
            $t("annotate.toolbar.9w3djd")
          }}
        </div>
        <div slot="content" v-if="!tagVisible">
          {{ $t("annotate.toolbar.8889r3") }}<br />{{
            $t("annotate.toolbar.qwmn4m")
          }}
        </div>
        <li @click="setMode(tagVisible ? 'HIDETAG' : 'SHOWTAG')">
          <i class="icon-aios_yincangleibie" v-if="tagVisible"></i>
          <i class="icon-aios_xianshileibie" v-if="!tagVisible"></i>
        </li>
      </el-tooltip>
      <li><i class="icon-aios_delT"></i></li>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.1gvw4s") }}<br />{{
            $t("annotate.toolbar.u9xvb9")
          }}
        </div>
        <li @click="setMode('HIDECURRENT')">
          <i class="icon-aios_yincang"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.xj7833") }}<br />{{
            $t("annotate.toolbar.p2o88s")
          }}
        </div>
        <li @click="setMode('HIDEALL')"><i class="icon-aios_eyeplus"></i></li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.o74dss") }}<br />{{
            $t("annotate.toolbar.8d28ee")
          }}
        </div>
        <li @click="setMode('SHOWCURRENT')">
          <i class="icon-aios_single2"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.ow4dh6") }}<br />{{
            $t("annotate.toolbar.h498mw")
          }}
        </div>
        <li @click="setMode('SHOWALL')">
          <el-badge :value="list.length" class="item">
            <i class="icon-aios_xianshixinxi"></i>
          </el-badge>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.b30jl7") }}</div>
        <li @click="setMode('ZOOMOUT')"><i class="icon-aios_zoomin1"></i></li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.q24t52") }}</div>
        <li @click="setMode('ZOOMIN')"><i class="icon-aios_zoomout1"></i></li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.t3yq14") }}</div>
        <li :class="{ active: modeType == 'MOVE' }" @click="setMode('MOVE')">
          <i class="icon-aios_hand"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">
          {{ $t("annotate.toolbar.12chlr") }}<br />{{
            $t("annotate.toolbar.6uo3pv")
          }}
        </div>
        <li @click="setMode('DELETECURRENT')">
          <i class="icon-aios_shanchu"></i>
        </li>
      </el-tooltip>
      <el-tooltip class="item" effect="dark" placement="right">
        <div slot="content">{{ $t("annotate.toolbar.3sydwh") }}</div>
        <li @click="setMode('DELETEALL')">
          <i class="icon-aios_clear_label"></i>
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
    <!-- <el-tooltip
      class="item"
      effect="dark"
      placement="right"
      v-if="$route.query.type != 1"
    >
      <div slot="content">Mark problem locations in the image and add descriptions</div>
      <div
        class="issue"
        :class="{ active: modeType == 'ISSUE' }"
        @click="setMode('ISSUE')"
      >
        <SvgIcon icon-name="icon-issue-mark" />
      </div>
    </el-tooltip> -->
  </div>
</template>

<script>
import SvgIcon from "@/components/svgIcon";
import { connect } from "echarts";
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
      list: [],
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
    dataList: {
      deep: true,
      handler(val) {
        console.info(val);
        this.list = val.filter((item) => {
          return !item.svgVisible || !item.tagVisible;
        });
      },
    },
  },
  created() {},
  mounted() {
    document.addEventListener("keydown", this.handleWatchEnter);
  },
  methods: {
    handleWatchEnter(e) {
      var key = window.event ? e.keyCode : e.which;
      if (key == 83) {
        this.setMode("");
      }
      if (key == 81) {
        this.setMode("RECT");
      }
      if (key == 87) {
        this.setMode("POLYGON");
      }
      if (key == 69) {
        this.setMode("POINT");
      }
      if (key == 90) {
        this.setMode("REVOKE");
      }
      if (key == 86) {
        this.setMode("RECOVER");
      }
      if (key == 70) {
        if (this.tagVisible) {
          this.setMode("HIDETAG");
        } else {
          this.setMode("SHOWTAG");
        }
      }
      if (key == 72) {
        this.setMode("HIDECURRENT");
      }
      if (key == 74) {
        this.setMode("HIDEALL");
      }
      if (key == 88) {
        this.setMode("SHOWCURRENT");
      }
      if (key == 66) {
        this.setMode("SHOWALL");
      }
      if (key == 109) {
        this.setMode("ZOOMOUT");
      }
      if (key == 107) {
        this.setMode("ZOOMIN");
      }
      if (key == 77) {
        this.setMode("MOVE");
      }
      if (key == 68) {
        this.setMode("DELETECURRENT");
      }
      if (key == 67) {
        this.setMode("DELETEALL");
      }
    },
    setMode(val) {
      console.info(val);
      switch (val) {
        case "RECT":
          if (this.modeType == "RECT") {
            this.modeType = "";
          } else {
            this.modeType = val;
          }
          break;
        case "POLYGON":
          if (this.modeType == "POLYGON") {
            this.modeType = "";
          } else {
            this.modeType = val;
          }
          break;
        case "POINT":
          if (this.modeType == "POINT") {
            this.modeType = "";
          } else {
            this.modeType = val;
          }
          break;
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
        case "HIDECURRENT":
          this.modeType = val;
          break;
        case "HIDEALL":
          this.allVisible = false;
          this.modeType = val;
          break;
        case "SHOWCURRENT":
          this.modeType = val;
          break;
        case "SHOWALL":
          this.allVisible = true;
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
        case "DELETECURRENT":
          this.modeType = val;
          break;
        case "DELETEALL":
          this.modeType = val;
          break;
        case "ISSUE":
          if (this.modeType == "ISSUE") {
            this.modeType = "";
          } else {
            this.modeType = val;
          }
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
