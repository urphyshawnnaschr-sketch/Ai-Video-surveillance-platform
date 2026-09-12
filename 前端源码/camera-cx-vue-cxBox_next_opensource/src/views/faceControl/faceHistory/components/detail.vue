<template>
  <el-dialog
    :title="$t('faceControl.faceRecognition.faceStore')"
    :visible.sync="visible"
    width="500px"
    @update:visible="(val) => $emit('update:visible', val)"
    append-to-body
  >
    <div style="overflow: hidden">
      <el-form
        label-position="right"
        :model="formatData"
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
      >
        <el-form-item :label="$t('components.detail.x993e6')">
            <el-radio-group v-model="type" size="mini" @input="getUser">
                <el-radio label="1" border>{{$t('components.detail.qki31y')}}</el-radio>
                <el-radio label="2" border>{{$t('components.detail.g1d6je')}}</el-radio>
            </el-radio-group>
        </el-form-item>
        
        <el-form-item
          :label="$t('faceControl.faceRecognition.belongingGroup')"
          prop="groupId"
          v-if="type==1"
        >
          <!-- <el-input :disabled="disabled" v-model="formatData.groupId"></el-input> -->
          <el-select
            class="pr10"
            style="width: 100%"
            :disabled="disabled"
            v-model="formatData.groupId"
            :placeholder="$t('common.chooseText')"
          >
            <el-option
              v-for="item in graps"
              :key="item.id"
              v-if="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('common.name')" :prop="type==1?'name':'userId'">
          <el-input v-if="type==1" :disabled="disabled" v-model="formatData.name"></el-input>
          <el-select
          v-else
            class="pr10"
            style="width: 100%"
            :disabled="disabled"
            v-model="formatData.userId"
          >
            <el-option
              v-for="item in userList"
              :key="item.id"
              v-if="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <span slot="footer" class="footer">
        <el-button
          size="mini"
          :disabled="disabled"
          type="primary"
          @click="save()"
          >{{ $t("button.sureText", { text: " " }) }}</el-button
        >
        <el-button size="mini" @click="closeDialog">{{
          $t("button.cancelText", { text: " " })
        }}</el-button>
      </span>
    </div>
  </el-dialog>
</template>
<script>
import { save, groupListData,addImageToExist,userList } from "../api";
export default {
  components: {},
  props: {
    visible: Boolean,
    currentItme: Object,
    currentState: String,
  },
  data() {
    return {
      type:'1',
      formatData: {
      },
      rules: {
        name: [{ required: true, message: $t('common.inputText'), trigger: "blur" }],
        groupId: [{ required: true, message: $t('common.chooseText'), trigger: "blur" }],
        userId: [{ required: true, message: $t('components.detail.dnka48'), trigger: "change" }],
      },
      graps: [],
      userList:[]
    };
  },
  computed: {
    title() {
      if (this.currentState == "see") {
        return $t("button.viewText", { text: '' });
      } else if (this.currentState == "add") {
        return $t("common.add", { text: "" });
      } else {
        return $t("common.edit", { text: "" });
      }
    },
    disabled() {
      if (this.currentState == "see") {
        return true;
      } else {
        return false;
      }
    },
  },
  created() {
    this.int();
  },
  methods: {
    async getUser(){
      if(this.type == '2'){
        this.userList = []
        const res = await userList()
        this.userList = res.data
      }
    },
    int() {
      this.getTable();
      //this.getDetail()
    },
    async getTable() {
      const { data, count } = await groupListData();
      this.graps = data;
    },
    // async getDetail(){
    //   if(this.currentItme.id){
    //     const { data,count } = await detail({id:this.currentItme.id})
    //     this.formatData = data
    //   }

    // },
    closeDialog() {
      this.$emit("update:visible", false);
      this.$emit("close", false);
    },

    validatePhoneNumber(phone) {
      // 定义手机号格式的正则表达式
      var regExp = /^1[3456789]\d{9}$/;

      if (regExp.test(phone)) {
        return true; // 符合手机号格式要求
      } else {
        return false; // 不符合手机号格式要求
      }
    },
    async save() {
      this.$refs.ruleForm.validate(async (valid) => { 
        if (valid) {
          if(this.type == '1'){
            save({
              ...this.formatData,
              reportId: this.currentItme.id,
            }).then(res=>{
              this.$message.success(this.$t('components.detail.u8pa20'));
              this.closeDialog();
            })
          }else{
            this.saveInfo()
          }
        }
      })
    },
    saveInfo(){
      let form = new FormData();
      form.append("userId", this.formatData.userId);
      form.append("reportId", this.currentItme.id);
      addImageToExist(form).then(res=>{
        if(res.data.success) {
          this.$message.success(this.$t('components.detail.u8pa20'));
          this.closeDialog();
        } else {
          this.$message.error(res.data.msg);
        }
      })
    },
    changes(val) {
      this.formatData.nameEn = val.replace(/[^a-zA-Z]/g, "");
    },
    handleUploadFile(value) {
      const files = value.target.files || {};
      // const formData = new FormData()
      // Array.from(files).forEach((item: any) => {
      //   formData.append('files', item)
      // })
      //调用后端接口，传入文件参数
      //const { data , code } = await API.fetchImageURl(formData)
      // if(code == 200){
      //   //TODO
      // }
      this.formatData.files = Object.values(files);
      this.$forceUpdate();
    },
    handleAddImg() {
      const input = document.querySelector("#upload");
      input.click();
    },
  },
};
</script>
<style scoped lang="scss">
.bg {
  background: white;
}
.wh {
  width: 100%;
  height: 100%;
}
.footer {
  float: right;
}
.iptcontent {
  position: relative;
  .img-input {
    opacity: 0;
  }
}
</style>
