<template>
  <el-dialog
    :title="$t('components.facemap.iyxqhk')"
    :visible.sync="visible"
    width="80%"
    append-to-body
    @closed="closed"
    top="3vh"
  >
    <div class="map">
      <el-row :gutter="20">
        <el-col :span="14">
          <div
            style="
              display: flex;
              align-items: center;
              justify-content: space-between;
              margin-bottom: 16px;
            "
          >
            <div class="grid-content">
              <span class="bg-purple-light">{{
                $t("mapimagemanagent.index.4585vg")
              }}</span>
            </div>
            <div style="display: flex; align-items: center">
              <div style="margin-left: 10px">
                <el-button
                  icon="el-icon-plus"
                  size="mini"
                  v-if="mapList.length == 0"
                  @click="addFun"
                >
                  {{ $t("common.add", { text: $t("common.layer") }) }}
                </el-button>
              </div>
            </div>
          </div>
          <div class="grid-content">
            <el-tabs
              v-model="configId"
              style="max-width: 800px; width: auto"
              type="card"
            >
              <el-tab-pane
                :name="item.id"
                v-for="(item, index) in mapList"
                :key="item.id"
              >
                <span slot="label">
                  <i class="el-icon-set-up"></i>{{ item.name }}
                </span>
                <images
                  v-if="item.id == configId"
                  :isAdd="isAdd"
                  ref="imagesed"
                  :is-add="isAdd"
                  :isEdit="isEdit"
                  :image-url="getImageUrl(item.filename)"
                  :markersEd="markersEd"
                  :editIndex="editIndex"
                  @addPosition="addPosition"
                  @edit="edit"
                  @objectDelete="objectDelete"
                />
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-col>
        <el-col :span="10">
          <div class="title-flex">
            <div class="grid-content">
              <span class="bg-purple-light">{{
                $t("mapimagemanagent.index.e1ixd0")
              }}</span>
            </div>
          </div>
          <div>
            <div class="tabContent">
              <div class="tabContent-light"></div>
              {{ $t("mapimagemanagent.index.x5ks36") }}
            </div>
            <el-table
              :data="tableData"
              style="width: 100%"
              :default-sort="{ prop: 'date', order: 'descending' }"
              height="240"
            >
              <el-table-column
                prop="cameraName"
                :label="$t('components.facemap.diybsv')"
              ></el-table-column>
              <el-table-column
                prop="groupName"
                :label="$t('faceControl.faceRecognition.belongingGroup')"
                sortable
                width="110px"
              ></el-table-column>
              <el-table-column prop="address" label="" width="160px">
                <template slot="header" slot-scope="scope">
                  <el-input
                    v-model="typeName"
                    size="mini"
                    @change="activeTab()"
                    :placeholder="$t('mapimagemanagent.index.8rny56')"
                  />
                </template>
                <template slot-scope="scope">
                  <el-button size="mini" @click="addMarker(scope.row)">{{
                    $t("mapimagemanagent.index.f35a6k")
                  }}</el-button>
                </template>
              </el-table-column>
            </el-table>
            <div class="tabContent" style="margin-top: 16px">
              <div class="tabContent-light"></div>
              {{ $t("mapimagemanagent.index.r75v41") }}
            </div>
            <el-table :data="tableDataEd" style="width: 100%" height="240">
              <el-table-column
                prop="cameraName"
                :label="$t('components.facemap.diybsv')"
              ></el-table-column>
              <el-table-column
                prop="groupName"
                :label="$t('faceControl.faceRecognition.belongingGroup')"
                sortable
                width="110px"
              ></el-table-column>
              <el-table-column prop="address" label="" width="160px">
                <template slot="header" slot-scope="scope">
                  <el-input
                    v-model="typeNamed"
                    size="mini"
                    :placeholder="$t('mapimagemanagent.index.8rny56')"
                  />
                </template>
                <template slot-scope="scope">
                  <div style="display: flex">
                    <el-button size="mini" @click="edit(scope)">{{
                      $t("mapimagemanagent.index.e7888l")
                    }}</el-button>
                    <el-button
                      size="mini"
                      type="danger"
                      @click="objectDelete(scope.row.id)"
                      >{{ $t("modeltesting.modeltesting.37bblo") }}</el-button
                    >
                  </div>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
        <el-col :span="24">
          <div
            class="bottom-btn"
            :style="{
              justifyContent: mapList.length > 0 ? 'space-between' : 'flex-end',
            }"
          >
            <div v-if="mapList.length > 0">
              <el-popconfirm
                :confirm-button-text="$t('button.sureText', { text: '' })"
                :cancel-button-text="$t('button.cancelText', { text: '' })"
                icon="el-icon-info"
                :title="$t('mapimagemanagent.index.dj6b6g')"
                @confirm="configDelete"
              >
                <el-button slot="reference" size="mini" type="danger">{{
                  $t("mapimagemanagent.index.x98964")
                }}</el-button>
              </el-popconfirm>
              <el-button
                style="margin-left: 16px"
                size="mini"
                @click="editFun"
                >{{
                  $t("common.edit", {
                    text: $t("mapimagemanagent.index.o49st7"),
                  })
                }}</el-button
              >
            </div>
            <div>
              <el-button type="primary" size="mini" @click="saveImage">{{
                $t("mapimagemanagent.index.0eju3m")
              }}</el-button>
            </div>
          </div>
        </el-col>
      </el-row>
      <el-dialog
        :title="$t('common.prompt')"
        :visible.sync="dialogVisibleAdd"
        width="50%"
        append-to-body
      >
        <el-form
          label-position="right"
          label-width="80px"
          :model="formLabelAlign"
        >
          <el-form-item :label="$t('mapimagemanagent.index.o3705e')">
            <el-input v-model="formLabelAlign.name"></el-input>
          </el-form-item>
          <el-form-item :label="$t('mapimagemanagent.index.yes7dj')">
            <el-upload
              ref="uploadDemo"
              class="upload-demo"
              drag
              :http-request="handleUploadDemo"
              :show-file-list="false"
              :limit="1"
              accept=".png,.jpg,.jpeg"
            >
              <div v-if="!formLabelAlign.filename">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">
                  {{ $t("modeltesting.videotest.9j6626")
                  }}<em>{{ $t("button.clickToUpload") }}</em>
                </div>
              </div>
              <img
                width="100%"
                v-else
                :src="getImageUrl(formLabelAlign.filename)"
                alt=""
              />
              <div class="el-upload__tip" slot="tip">
                {{ $t("mapimagemanagent.index.s2m22t") }}
              </div>
            </el-upload>
          </el-form-item>
        </el-form>
        <span slot="footer" class="dialog-footer">
          <el-button @click="dialogVisibleAdd = false">{{
            $t("button.cancelText", { text: " " })
          }}</el-button>
          <el-button type="primary" @click="addConfigSave">{{
            $t("button.sureText", { text: " " })
          }}</el-button>
        </span>
      </el-dialog>
    </div>
  </el-dialog>
</template>
<script>
import images from "./images.vue";
import {
  upload,
  configList,
  configSave,
  configDelete,
  cameraList,
  cameraSave,
  cameraDelete,
} from "../faceManagent/api";
import Cookies from "js-cookie";
export default {
  components: {
    images,
  },
  data() {
    return {
      visible: true,
      groupValue: "",
      activeName: 0,
      mapList: [],
      tableData: [],
      tableDataEd: [],
      markersEd: [],
      dialogVisibleAdd: false,
      formLabelAlign: {
        name: "",
        type: "",
        rules: [],
      },
      token: Cookies.get("X-Token"),
      VUE_APP_API_BASE_URL,
      configType: 0,
      configId: null,
      currentDevice: {},
      isEdit: false,
      isAdd: false,
      editIndex: 0,
      typeName: "",
      typeNamed: "",
    };
  },
  created() {
    this.getConfigList();
  },
  mounted() {},
  methods: {
    // 关闭
    closed() {
      this.$emit("close");
    },

    async handleUploadDemo(files) {
      console.info(files);
      const file = files.file;
      let form = new FormData();
      form.append("file", file);
      const res = await upload(form);
      this.$set(this.formLabelAlign, "filename", res.data);
      this.$message.success(this.$t("addproject.index.92j503"));
      this.$refs.uploadDemo.clearFiles();
    },
    addFun() {
      this.formLabelAlign = {};
      this.dialogVisibleAdd = true;
    },
    editFun() {
      let config = this.mapList.find((item) => item.id === this.configId);
      this.formLabelAlign = config;
      this.dialogVisibleAdd = true;
    },
    async getConfigList() {
      const res = await configList();
      this.mapList = res.data;
      if (this.mapList.length > 0) {
        this.configId = this.mapList[0].id;
        this.configType = this.mapList[0].type;
        this.$nextTick(() => {
          this.activeTab();
        });
      }
    },
    async addConfigSave() {
      const data = {
        ...this.formLabelAlign,
        state: this.configType == 0 ? 1 : 2,
      };
      configSave(data).then((res) => {
        if (res.code == 0) {
          this.dialogVisibleAdd = false;
          this.getConfigList();
          this.$message.success(
            $t("button.addText", { text: $t("common.success") })
          );
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    async configDelete() {
      configDelete({ id: this.configId }).then((res) => {
        if (res.code == 0) {
          this.getConfigList();
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    delRules(scope) {
      this.$confirm(
        this.$t("mapimagemanagent.index.25lmtn"),
        $t("common.prompt"),
        {
          confirmButtonText: $t("button.sureText", { text: "" }),
          cancelButtonText: $t("button.cancelText", { text: "" }),
          type: "warning",
        }
      )
        .then(() => {
          this.formLabelAlign.rules = this.formLabelAlign.rules.filter(
            (item, index) => index != scope.$index
          );
        })
        .catch(() => {
          this.$message({
            type: "info",
            message: this.$t("mapimagemanagent.index.516532"),
          });
        });
    },
    ///删除设备
    objectDelete(id) {
      cameraDelete({
        id: id,
      }).then((res) => {
        if (res.code == 0) {
          this.getCameraList();
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    //获取摄像头
    async getCameraList() {
      const res = await cameraList({
        configId: this.configId,
      });
      this.tableData = res.data.unmarkList;
      this.tableDataEd = res.data.markedList;
      this.markersEd = res.data.markedList.map((item) => {
        return {
          originX: item.position[0],
          originY: item.position[1],
          ...item,
        };
      });
    },
    activeTab() {
      this.tableData = [];
      this.markersEd = [];
      this.tableDataEd = [];
      this.$refs.imagesed[0].clearMarkers();
      this.getCameraList();
    },

    //未添加的设备允许添加图标
    addMarker(row) {
      if (this.isAdd || this.isEdit) {
        this.$message.error(this.$t("mapimagemanagent.index.u86c76"));
        return;
      }
      this.isAdd = true;
      this.isEdit = false;
      this.currentDevice = row;
    },
    edit(scope) {
      if (this.isAdd || this.isEdit) {
        this.$message.error(this.$t("mapimagemanagent.index.u86c76"));
        return;
      }
      this.editIndex = scope.$index;
      this.isEdit = true;
      this.isAdd = false;
      this.currentDevice = scope.row;
    },
    //
    addPosition(data) {
      this.currentDevice.position = [
        parseInt(data.originX),
        parseInt(data.originY),
        data.x,
        data.y,
      ];
    },
    async saveImage() {
      let data = {
        id: this.currentDevice.id,
        configId: this.configId,
        cameraId: this.currentDevice.cameraId,
        position: this.currentDevice.position,
      };
      if (!data.position || data.position.length < 4) {
        this.$message.error(this.$t("mapimagemanagent.index.p3xe4l"));
        return;
      }
      const res = await cameraSave(data);
      if (res.code === 0) {
        this.isAdd = false;
        this.isEdit = false;
        this.activeTab();
        this.$message({
          type: "success",
          message: $t("button.saveText", { text: $t("common.success") }),
        });
      }
    },
    // 获取图片
    getImageUrl(filename) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/face/track/config/image?filename=${filename}&X-Token=${token}`;
    },
  },
};
</script>
<style scoped lang="scss">
.map {
  background-color: #fff;
  padding: 10px;

  .grid-content {
    display: flex;
    justify-content: flex-start;
  }
  .title-flex {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
  }
  ::v-deep .el-button--small {
    padding: 13px 20px;
  }

  .bg-purple-light {
    font-family: PingFang SC;
    font-weight: 600;
    font-size: 16px;
    line-height: 20px;
    letter-spacing: -1%;
    color: #1c1f23;
    border-bottom: 2px solid #eb3a2f;
    padding-bottom: 8px;
  }

  .tabs {
    border-radius: 2px;
    padding: 3px;
    background: #f2f3f5;
    display: flex;
    // margin: 16px 0;

    .tabItems {
      width: 88px;
      border-radius: 1px;
      padding: 2px 12px;
      background-color: transparent;
      font-family: PingFang SC;
      font-weight: 500;
      font-size: 16px;
      line-height: 24px;
      letter-spacing: 0px;
      vertical-align: middle;
      text-align: center;
      cursor: pointer;
    }

    .tabItemsActive {
      background-color: white;
      color: #E53935;
    }
  }

  .tabContent {
    margin-bottom: 8px;
    font-family: PingFang SC;
    font-weight: 600;
    font-size: 13px;
    line-height: 20px;
    letter-spacing: -1%;
    color: #1c1f23;
    display: flex;
    align-items: center;
    justify-content: flex-start;

    .tabContent-light {
      background-color: #eb3a2f;
      width: 3px;
      height: 14px;
      border-radius: 2px;
      display: inline-block;
      margin-right: 8px;
    }
  }

  .bottom-btn {
    display: flex;
    align-items: center;
    margin-top: 20px;
  }
}
.el-dropdown-link {
  font-size: 12px;
  color: #1A0808;
}
:deep(.el-upload) {
  text-align: left !important;
}
</style>
