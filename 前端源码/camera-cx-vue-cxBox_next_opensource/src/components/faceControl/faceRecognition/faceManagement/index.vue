<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('facemanagement.index.i1cm6i')"
      :visible.sync="dialogVisible"
      width="800px"
      @closed="closed"
    >
      <div class="head-container">
        <el-upload
          action=""
          :http-request="handleUploadImg"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.gif"
          style="display: inline"
        >
          <el-button type="primary" icon="el-icon-plus">{{$t('facemanagement.index.524wk8')}}</el-button>
        </el-upload>
        <el-button
          icon="el-icon-refresh-right"
          style="float: right"
          @click="getFaceList"
        ></el-button>
      </div>
      <div>
        <el-table
          v-loading="loading"
          :data="tableData"
          border
          style="width: 100%"
        >
          <el-table-column align="center" prop="name" :label="$t('facemanagement.index.3rnra8')">
          </el-table-column>
          <el-table-column align="center" prop="tel" :label="$t('facemanagement.index.77wtu4')">
          </el-table-column>
          <el-table-column
            align="center"
            prop="remark"
            :label="$t('common.remark')"
          >
          </el-table-column>
          <el-table-column
            align="center"
            :label="$t('common.action', { text: '' })"
            width="160"
          >
            <template slot-scope="scope">
              <el-button type="text" @click="lookData(scope.row)">{{
                $t("button.viewText", { text: "" })
              }}</el-button>
              <el-button
                type="text"
                class="danger"
                @click="deleteData(scope.row)"
                >{{ $t("button.deleteText", { text: "" }) }}</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination">
          <el-pagination
            background
            :current-page="params.page"
            :page-size="params.limit"
            layout="total, sizes, prev, pager, next, jumper"
            :total="total"
            @current-change="handleCurrentChange"
            @size-change="handleSizeChange"
          ></el-pagination>
        </div>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :close-on-click-modal="false"
      :title="$t('facemanagement.index.61rp1h')"
      :visible.sync="faceDialogVisible"
      width="600px"
    >
      <el-form :model="form" label-width="80px">
        <el-form-item>
          <img class="img" :src="$common.handleFace(form.id)" />
        </el-form-item>
        <el-form-item :label="$t('facemanagement.index.1jnnxa')">{{ form.type }}</el-form-item>
        <el-form-item :label="$t('facemanagement.index.3rnra8')">
          <el-input v-model="form.name" :placeholder="$t('facemanagement.index.j66758')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('facemanagement.index.77wtu4')">
          <el-input v-model="form.tel" :placeholder="$t('facemanagement.index.f88715')"></el-input>
        </el-form-item>
        <el-form-item :label="$t('alarmdetail.newdetail.uxx7hx')">
          <el-input
            v-model="form.remark"
            :placeholder="$t('facemanagement.index.niffj3')"
          ></el-input>
        </el-form-item>
        <el-form-item :label="$t('common.createTime')">{{
          form.create
        }}</el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="faceDialogVisible = false">{{
          $t("button.cancelText", { text: "" })
        }}</el-button>
        <el-button type="primary" @click="saveData">{{
          $t("button.submitText")
        }}</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import md5 from "js-md5";
import {
  getFaceList,
  getFaceDetail,
  deleteFace,
  saveFace,
  checkFile,
  checkChunk,
  chunkUpload,
  merge,
} from "@/api/faceControl/faceRecognition";
export default {
  props: {
    currentFaceType: {
      type: String,
      default: "",
    },
  },
  data() {
    return {
      loading: false,
      dialogVisible: true,
      faceDialogVisible: false,
      params: {
        page: 1,
        limit: 10,
        type: this.currentFaceType,
      },
      total: 0,
      tableData: [],
      form: {},
    };
  },
  async created() {
    await this.getFaceList();
  },
  methods: {
    // 获取数据列表
    async getFaceList() {
      this.loading = true;
      const data = await getFaceList(this.params);
      this.loading = false;
      this.tableData = data.data;
      this.total = Number(data.count);
    },
    // 查看人脸
    async lookData(item) {
      const data = await getFaceDetail({ id: item.id });
      this.form = data.data;
      Object.assign(this.form, {
        tel: data.data.tel == "/" ? "" : data.data.tel,
        remark: data.data.remark == "/" ? "" : data.data.remark,
      });
      this.faceDialogVisible = true;
    },
    // 删除人脸
    deleteData(item) {
      this.$confirm($t("modal.deleteSureText"), $t("common.prompt"), {
        confirmButtonText: $t("button.sureText", { text: "" }),
        cancelButtonText: $t("button.cancelText", { text: "" }),
        type: "warning",
      }).then(async () => {
        const res = await deleteFace({ id: item.id });
        if (res.code == 0) {
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
          await this.getFaceList();
        }
      });
    },
    // 保存人脸数据
    async saveData() {
      const params = {
        id: this.form.id,
        name: this.form.name,
        tel: this.form.tel,
        remark: this.form.remark,
      };
      await saveFace(params);
      this.$message.success(
        $t("button.saveText", { text: $t("common.success") })
      );
      this.faceDialogVisible = false;
      this.getFaceList();
    },
    // 上传
    async handleUploadImg(files) {
      const file = files.file;
      const res1 = await checkFile({
        md5File: md5(file.name),
        fileName: file.name,
      });
      const res2 = await checkChunk({
        md5File: md5(file.name),
        chunk: 0,
      });
      let formData = new FormData();
      formData.append("name", file.name);
      formData.append("type", file.type);
      formData.append("lastModifiedDate", file.lastModifiedDate);
      formData.append("size", file.size);
      formData.append("file", file);
      formData.append("md5File", md5(file.name));
      const res3 = await chunkUpload(formData);
      const res4 = await merge({
        md5File: md5(file.name),
        name: file.name,
        chunks: 1,
        type: this.currentFaceType,
      });
      this.$message.success(this.$t('addproject.index.92j503'));
      this.getFaceList();
    },
    // 关闭回调
    closed() {
      this.$emit("close");
    },
    // 分页
    handleCurrentChange(val) {
      this.params.page = val;
      this.getFaceList();
    },
    handleSizeChange(val) {
      this.params.limit = val;
      this.params.page = 1;
      this.getFaceList();
    },
  },
};
</script>
<style scoped lang="scss">
.img {
  width: 180px;
  display: block;
}
</style>
