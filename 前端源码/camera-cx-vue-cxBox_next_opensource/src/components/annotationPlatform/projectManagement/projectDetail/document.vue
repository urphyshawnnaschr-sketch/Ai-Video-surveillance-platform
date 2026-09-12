<template>
  <div>
    <el-button
      type="primary"
      icon="el-icon-download"
      @click="download"
      :disabled="!this.projectDetail.docFileId"
      >{{$t('projectdetail.document.1op5b2')}}</el-button
    >
    <div class="doc" v-html="vHtml" v-loading="loading"></div>
  </div>
</template>
<script>
import { getDetail } from "@/api/annotationPlatform/projectManagement";
import mammoth from "mammoth";
export default {
  data() {
    return {
      loading: false,
      projectId: this.$route.query.id,
      projectDetail: {},
      vHtml: "",
    };
  },
  async created() {
    this.getDetail();
  },
  methods: {
    async getDetail() {
      const data = await getDetail({ id: this.projectId });
      this.projectDetail = data.data;
      if (this.projectDetail.docFileId) {
        this.readExcelFromRemoteFile(
          this.$common.handleFile(this.projectDetail.docFileId)
        );
      }
    },
    readExcelFromRemoteFile(url) {
      this.loading = true;
      var vm = this;
      var xhr = new XMLHttpRequest();
      xhr.open("get", url, true);
      xhr.responseType = "arraybuffer";
      xhr.onload = function () {
        if (xhr.status == 200) {
          mammoth
            .convertToHtml({ arrayBuffer: new Uint8Array(xhr.response) })
            .then(function (resultObject) {
              vm.$nextTick(() => {
                vm.loading = false;
                vm.vHtml = resultObject.value;
              });
            })
            .catch(function(e) { // 注意这里的修改
              vm.loading = false;
              console.error("Conversion error: ", e);
            });
        }
      };
      xhr.send();
    },
    download() {
      location.href = this.$common.handleFile(this.projectDetail.docFileId);
    },
  },
};
</script>
<style scoped lang="scss">
.doc {
  width: 100%;
  min-height: 600px;
  overflow-y: auto;
}
</style>
