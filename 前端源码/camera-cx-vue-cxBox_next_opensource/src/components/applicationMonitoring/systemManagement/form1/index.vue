<template><div><el-form ref="form1" :model="form1" label-width="140px"><el-form-item
        :label="$t('common.ipAddress')"
        prop="ipAddr"
        :rules="[{required: true, message: $t('form1.index.md27qc'), trigger:'blur'}]"
      ><el-input v-model="form1.ipAddr"></el-input><div class="tip__text">{{$t('form1.index.7s784w')}}</div></el-form-item><template v-if="isRecordEnable"><el-divider></el-divider><el-form-item
          :label="$t('form1.index.23i2r7')"
          prop="isRecord"
          :rules="[{required: true, message: $t('common.chooseText'), trigger:'change',},]"
        ><el-radio v-model="form1.isRecord" label="true">{{$t('applicationMonitoring.boxManagement.tkhb19')}}</el-radio><el-radio v-model="form1.isRecord" label="false">{{$t('applicationMonitoring.boxManagement.q827fn')}}</el-radio></el-form-item><el-form-item :label="$t('form1.index.j5762b')"><el-checkbox-group v-model="dayList"><el-checkbox :label="10">{{$t('form1.index.76o018')}}</el-checkbox><el-checkbox :label="20">{{$t('form1.index.1j8n5u')}}</el-checkbox><el-checkbox :label="30">{{$t('form1.index.657fe2')}}</el-checkbox><el-checkbox :label="40">{{$t('form1.index.7b57gx')}}</el-checkbox><el-checkbox :label="50">{{$t('form1.index.31086s')}}</el-checkbox><el-checkbox :label="60">{{$t('form1.index.qnr2jw')}}</el-checkbox><el-checkbox :label="70">{{$t('form1.index.i5658j')}}</el-checkbox></el-checkbox-group></el-form-item><el-form-item :label="$t('form1.index.290p47')" prop="recordTimes"><el-time-picker
            is-range
            arrow-control
            v-model="recordTimes"
            :range-separator="$t('common.pickerDate.to')"
            :start-placeholder="$t('addcamera.timeinfo.5r5j4x')"
            :end-placeholder="$t('addcamera.timeinfo.dc2916')"
            :placeholder="$t('addcamera.timeinfo.yeo0o0')"
          ></el-time-picker></el-form-item><el-form-item :label="$t('form1.index.d5d44h')"><el-input-number
            v-model="form1.recordSecs"
            :min="15"
            :max="300"
            :step="5"
            :label="$t('form1.index.t63q15')"
          ></el-input-number></el-form-item></template>

      <!-- <el-form-item label="推理地址">
        <el-input v-model="form1.algorithmUrl"></el-input>
        <div class="tip__text">
          模型测试时调用的推理地址，一般由算法模型方提供
        </div>
      </el-form-item>
      <el-divider></el-divider> -->
      <!-- <el-form-item label="流媒体视频流类型" prop="streamType">
        <el-radio v-model="form1.streamType" label="rtsp">视频流</el-radio>
        <el-radio v-model="form1.streamType" label="algo">算法视频流</el-radio>
      </el-form-item>
      <el-divider></el-divider> -->

      <!-- <el-form-item label="流媒体服务器地址">
        <el-input v-model="form1.mediaUrl"></el-input>
        <div class="tip__text">
          流媒体服务器内网访问地址，For example：http://192.168.0.150:7088
        </div>
      </el-form-item>
      <el-form-item label="流媒体播放地址">
        <el-input v-model="form1.playUrl"></el-input>
        <div class="tip__text">
          流媒体播放外网访问地址，For example：http://domain:8088
        </div>
      </el-form-item>
      <el-divider></el-divider>
      <el-form-item label="算法推流IP">
        <el-input v-model="form1.pushIp"></el-input>
        <div class="tip__text">流媒体服务器内网地址，For example：192.168.0.150</div>
      </el-form-item>
      <el-form-item label="算法推流端口">
        <el-input v-model="form1.pushPort"></el-input>
        <div class="tip__text">流媒体服务器端口，For example：554</div>
      </el-form-item> -->

      <el-form-item>
        <el-button type="primary" @click="onSubmitForm('form1')"
          >{{$t('marktool.annotate.q5fjzf')}}</el-button
        >
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import {
  getListDataDetail,
  saveData,
} from "@/api/applicationMonitoring/systemManagement";
export default {
  props: {
    form1: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {
      loading: false,
      title: this.currentId
        ? $t("common.edit", { text: $t("common.config") })
        : $t("common.add", { text: $t("common.config") }),
      dialogVisible: true,
      params: {
        id: this.currentId,
        name: "",
        tag: "",
        val: "",
      },
      rules: {
        name: [{ required: true, message: this.$t('addsystem.index.o3leu1'), trigger: "blur" }],
        tag: [{ required: true, message: this.$t('addsystem.index.0x30l1'), trigger: "blur" }],
        val: [{ required: true, message: this.$t('addsystem.index.0x30l1'), trigger: "blur" }],
      },
      tableData: [],
      recordTimes: [
        new Date(2016, 9, 10, 0, 0, 0),
        new Date(2016, 9, 10, 23, 59, 59),
      ],
      dayList: [],
      isRecordEnable: false,
    };
  },
  async created() {
    if (this.currentId) {
      await this.getListDataDetail();
    }

    // 判断是否启用录制功能
    let record_enabled = Cookies.get("record_enabled");
    if (record_enabled == "true") {
      this.isRecordEnable = true;
    }
  },
  watch: {
    form1(newVal, oldVal) {
      // 解析录像日期
      if (newVal.recordDates && newVal.recordDates != "") {
        let days = newVal.recordDates.split(",");
        let idays = [];
        days.forEach((item) => {
          idays.push(parseInt(item));
        });
        this.dayList = idays;
      }

      // 解析录像时段
      if (newVal.recordTimes && newVal.recordTimes != "") {
        let days = newVal.recordTimes.split(",");
        let idates = [];
        let hasErr = false;
        days.forEach((item) => {
          try {
            let hours = item.slice(0, 2);
            let minutes = item.slice(2, 4);
            let seconds = item.slice(4, 6);

            if (hours && minutes && seconds) {
              idates.push(
                new Date(
                  2016,
                  9,
                  10,
                  parseInt(hours),
                  parseInt(minutes),
                  parseInt(seconds)
                )
              );
            } else {
              hasErr = true;
            }
          } catch (e) {
            hasErr = true;
          }
        });

        if (!hasErr) {
          this.recordTimes = idates;
        } else {
          idates = [];
          idates.push(new Date(2016, 9, 10, 0, 0, 0));
          idates.push(new Date(2016, 9, 10, 23, 59, 59));
          this.recordTimes = idates;
        }
      }
    },
  },
  methods: {
    // 格式化时分秒
    getTimeStr(d) {
      let hours = d.getHours();
      let minutes = d.getMinutes();
      let seconds = d.getSeconds();
      hours = hours < 10 ? "0" + hours : hours;
      minutes = minutes < 10 ? "0" + minutes : minutes;
      seconds = seconds < 10 ? "0" + seconds : seconds;
      return hours + "" + minutes + "" + seconds;
    },

    // 提交表单
    onSubmitForm(formName) {
      let _recordDates = this.dayList.join(",");
      let sTime = this.getTimeStr(this.recordTimes[0]);
      let eTime = this.getTimeStr(this.recordTimes[1]);
      let _recordTimes = sTime + "," + eTime;

      this.$refs[formName].validate(async (valid) => {
        if (valid) {
          Object.assign(this.form1, {
            recordDates: _recordDates,
            recordTimes: _recordTimes,
          });
          this.$emit("submitForm", "form1", this.form1);
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
  },
};
</script>
<style scoped lang="scss"></style>
