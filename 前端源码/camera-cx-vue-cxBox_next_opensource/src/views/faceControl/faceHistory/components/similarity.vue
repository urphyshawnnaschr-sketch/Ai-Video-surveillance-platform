<template>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('components.similarity.cb1l2r')"
      :visible.sync="dialogVisible"
      width="400px"
      @closed="cancelFun"
    >
        <el-form ref="form" :model="params" :rules="rules" label-width="90px">
            
            <el-form-item :label="$t('components.similarity.46ept0')" prop="minSimiliarity">
                <el-input-number
                :placeholder="$t('common.inputText')"
                v-model="params.minSimiliarity"
                :min="0.01"
                :controls="false"
                :precision="2"
                class="input-num"
              ></el-input-number>
            </el-form-item>
            <el-form-item :label="$t('components.similarity.lb458m')" prop="sameSimiliarity">
                <el-input-number
                :placeholder="$t('common.inputText')"
                v-model="params.sameSimiliarity"
                :min="0.01"
                :controls="false"
                :precision="2"
                class="input-num"
              ></el-input-number>
            </el-form-item>
            <div style="text-align: right;">
                <el-button @click="cancelFun">{{$t("button.cancelText", { text: "" })}}</el-button>
                <el-button type="primary" @click="saveDay">{{$t("button.saveText", { text: "" })}}</el-button>
            </div>
        </el-form>
    </el-dialog>
</template>

<script>
import { getFaceSimiliarity,saveFaceSimiliarity } from '../api';
export default {
  data() {
    return {
      dialogVisible:true,
      params:{
        minSimiliarity:null,
        sameSimiliarity:null,
      },
      rules:{
        minSimiliarity: [{ required: true, message: $t('components.similarity.7jdykb'), trigger: "blur" }],
        sameSimiliarity: [{ required: true, message: $t('components.similarity.2744lt'), trigger: "blur" }],
      },
    };
  },
  created(){
    this.getData();
  },
  methods:{
    async getData(){
      const res = await getFaceSimiliarity();
      this.params = res.data
    },
    cancelFun(){
        this.$emit('close')
    },
    saveDay(){
      saveFaceSimiliarity(this.params).then(res=>{
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
<style lang="scss">
.input-num {
  .el-input__inner {
    text-align: left !important;
  }
}
</style>
