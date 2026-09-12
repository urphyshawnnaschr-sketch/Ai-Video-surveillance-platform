<template>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('components.saveterm.arhpig')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="cancelFun"
    >
        <div class="tip" style="margin-bottom: 15px;">{{$t('components.saveterm.mwy897')}}</div>
        <el-form ref="form" :model="params" :rules="rules" label-width="160px">
            
            <el-form-item :label="$t('components.saveterm.7g8e3c')" prop="clearReportDay">
                <el-select
                    :placeholder="$t('common.chooseText')"
                    v-model="params.clearReportDay"
                    
                >
                    <el-option
                    v-for="(item, index) in clearDayList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                    ></el-option>
                </el-select>
                <div class="tip">{{$t('applicationMonitoring.alarmmanagement.4857w4')}}</div>
            </el-form-item>
            <div style="text-align: right;">
                <el-button @click="cancelFun">{{$t("button.cancelText", { text: "" })}}</el-button>
                <el-button type="primary" @click="saveDay">{{$t("button.saveText", { text: "" })}}</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
import { getAfterSales } from "@/api/applicationMonitoring/systemManagement";
import { saveClearFaceReportDayConfig } from '../api';
export default {
  data() {
    return {
      dialogVisible:true,
      params:{
        clearReportDay:"30",
      },
      rules:{
        clearReportDay: [{ required: true, message: $t('common.chooseText'), trigger: "change" }],
      },
      clearDayList:[
        {
          id:'30',
          name:$t('applicationMonitoring.alarmmanagement.0d5feh')
        },{
          id:'20',
          name:$t('applicationMonitoring.alarmmanagement.gmv893')
        },{
          id:'14',
          name:$t('applicationMonitoring.alarmmanagement.f320ly')
        },{
          id:'10',
          name:$t('applicationMonitoring.alarmmanagement.6f383w')
        },{
          id:'7',
          name:$t('applicationMonitoring.alarmmanagement.852fko')
        },{
          id:'5',
          name:$t('applicationMonitoring.alarmmanagement.m84bsz')
        },{
          id:'3',
          name:$t('applicationMonitoring.alarmmanagement.87kl0z')
        },{
          id:'1',
          name:$t('applicationMonitoring.alarmmanagement.65oh86')
        },
        {
          id:'0',
          name:$t('faceControl.faceRecognition.today')
        },
      ],
      clearReportDay:"30",
    };
  },
  created(){
    this.getDay()
  },
  methods:{
    // 切换定时任务清除告警信息天数
    saveDay(){
      let str = "";
      this.clearDayList.forEach(item=>{
        if(this.params.clearReportDay==item.id){
          str = item.name
        }
      })
      this.$confirm(`${$t("applicationMonitoring.alarmmanagement.4857w2", [
          str,
        ])}<div style="color: red;">${$t(
          "applicationMonitoring.alarmmanagement.4857w3",
          [str]
        )}</div>`, $t("common.prompt"),{
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          dangerouslyUseHTMLString: true,
          type: "warning",
        }).then(() => {
          saveClearFaceReportDayConfig({clearFaceReportDay:this.params.clearReportDay}).then(res=>{
            this.$message({
              message: $t("button.saveText", { text: $t("common.success") }),
              type: 'success',
              duration:500
            })
            this.oldDay = this.params.clearReportDay;
            this.$emit('close')
          })
        }).catch(() => {
          this.params.clearReportDay = this.oldDay;
        });
    },
    //获取清除告警信息天数
    async getDay(){
        const res = await getAfterSales({tag:'clearFaceReportDay'});
        this.params.clearReportDay = res.data?res.data:'30'
        this.oldDay = res.data?res.data:'30'
    },
    cancelFun(){
        this.$emit('close')
    }
  }
};
</script>
<style scoped lang="scss">
.tip{
    color: #A8ABB2;
    font-size: 12px;
}
</style>
