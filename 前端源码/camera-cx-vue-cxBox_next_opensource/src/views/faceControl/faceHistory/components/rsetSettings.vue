<template>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('components.rsetsettings.1md222')"
      :visible.sync="dialogVisible"
      width="600px"
      @closed="cancelFun"
    >
        <div class="tip" style="margin-bottom: 15px;">{{$t('components.rsetsettings.sbdzy1')}}</div>
        <el-form ref="form" :model="params" :rules="rules" label-width="120px">
            
            <el-form-item :label="$t('components.rsetsettings.l6233w')" prop="minute">
                <el-select
                    :placeholder="$t('common.chooseText')"
                    v-model="params.minute"
                    
                >
                    <el-option
                    v-for="(item, index) in minuteList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                    ></el-option>
                </el-select>
            </el-form-item>
            <div style="text-align: right;">
                <el-button @click="cancelFun">{{$t("button.cancelText", { text: "" })}}</el-button>
                <el-button type="primary" @click="saveDay">{{$t("button.saveText", { text: "" })}}</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
import { duplicate,duplicateInfo } from '../api';
export default {
  data() {
    return {
      dialogVisible:true,
      params:{
        minute:"",
      },
      rules:{
        minute: [{ required: true, message: $t('common.chooseText'), trigger: "change" }],
      },
      minuteList:[
        {
          id:'30',
          name:'30s'
        },{
          id:'60',
          name:'1min'
        },{
          id:'300',
          name:'5min'
        },
      ],
    };
  },
  created(){
    this.getData();
  },
  methods:{
    async getData(){
      const res = await duplicateInfo();
      this.params.minute = res.data
    },
    cancelFun(){
        this.$emit('close')
    },
    saveDay(){
      duplicate(this.params).then(res=>{
        this.$message({
          message: $t("button.saveText", { text: $t("common.success") }),
          type: 'success',
          duration:500
        });
        this.$emit('close')
      })
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
