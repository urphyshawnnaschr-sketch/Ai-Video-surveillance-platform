<template>
  <el-dialog
    :close-on-click-modal="false"
    :title="$t('voicemanagement.addvoice.5t82l9')"
    :visible.sync="dialogVisible"
    width="700px"
    @close="handleDialogClose"
  >
    <div style="padding-bottom: 10px; margin-top: -15px">
      <el-button type="primary" icon="el-icon-plus" @click="addData">{{
        $t("voicemanagement.addvoice.761i3w")
      }}</el-button>
    </div>
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column
        align="center"
        prop="name"
        :label="$t('common.associationAlgorithm')"
      >
      </el-table-column>
      <el-table-column
        align="center"
        prop="soundFile"
        :label="$t('voicemanagement.index.e4e6j3')"
      >
      </el-table-column>
      <el-table-column
        align="center"
        :label="$t('voicemanagement.index.16j7t7')"
        width="100"
      >
        <template slot-scope="scope">
          <!-- {{ scope.row }} -->
          <span
            class="el-icon-video-play"
            style="font-size: 16px"
            v-if="!scope.row.isPlay"
            @click="Voiceplay(scope.$index)"
          ></span>
          <span
            v-if="scope.row.isPlay"
            class="el-icon-video-pause"
            style="color: #E53935; font-size: 16px"
            @click="VoicePause(scope.$index)"
          ></span>
          <audio
            :ref="'audio' + scope.$index"
            @ended="onAudioEnded(scope.$index)"
          >
            <source
              :src="getAlarmMp3Url(scope.row.id)"
            />
          </audio>
        </template>
      </el-table-column>
      <el-table-column
        align="center"
        :label="$t('common.action', { text: '' })"
        width="100"
      >
        <template slot-scope="scope">
          <el-button
            type="text"
            style="color: #E53935; margin-right: 10px"
            @click="editData(scope.row)"
            >{{ $t("common.edit", { text: "" }) }}</el-button
          >
          <!-- <el-button type="text" class="danger" @click="deleteData(scope.row)">删除
                    </el-button> -->
          <el-popconfirm
            :confirm-button-text="$t('button.deleteText', { text: '' })"
            :cancel-button-text="$t('button.cancelText', { text: '' })"
            @confirm="deleteData(scope.row)"
            :title="$t('voicemanagement.index.36254z')"
          >
            <el-button type="text" class="danger" slot="reference">{{
              $t("button.deleteText", { text: "" })
            }}</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <!-- 新增、编辑 -->
    <AddVoice
      v-if="addVisible"
      :algorithmList="algorithmList"
      :currentObj="currentObj"
      @closeAdd="closeAdd"
    />
  </el-dialog>
</template>

<script>
import Cookies from "js-cookie";
import {
  getListData,
  deleteSoundFile,
} from "@/api/applicationMonitoring/modelTesting";
import AddVoice from "./addVoice.vue";
export default {
  components: {
    AddVoice,
  },
  data() {
    return {
      dialogVisible: true,
      addVisible: false,
      tableData: [],
      oldData: [],
      loading: false,
      currentObj: {},
      algorithmList: [],
      VUE_APP_API_BASE_URL,
      KeyNum: 0,
    };
  },
  created() {
    this.getListData();
  },
  methods: {
    // 获取算法列表
    async getListData() {
      this.loading = true;
      this.algorithmList = [];
      this.tableData = [];
      const data = await getListData();
      this.loading = false;
      this.algorithmList = data.data;
      let arr = data.data.filter((item) => item.soundFile);
      let NewArr = [];
      if (arr && arr.length > 0) {
        arr.forEach((item) => {
          // item.isPlay=false
          NewArr.push({
            name: item.name,
            id: item.id,
            soundFile: item.soundFile,
            isPlay: false,
          });
        });
        this.tableData = NewArr;
      }
    },
    handleDialogClose() {
      this.$emit("closeHandle");
      this.getListData();
    },
    // 新增
    addData() {
      this.addVisible = true;
    },
    // 编辑
    editData(data) {
      this.currentObj = data;
      this.addVisible = true;
    },
    closeAdd() {
      this.addVisible = false;
      this.getListData();
    },
    // 删除
    deleteData(data) {
      console.log(123);
      deleteSoundFile({ id: data.id }).then((res) => {
        this.$message.success(
          $t("button.deleteText", { text: $t("common.success") })
        );
        this.getListData();
      });
    },
    // 音频播放
    Voiceplay(index) {
      let oldIndex = index;
      this.tableData.forEach((item, ind) => {
        item.isPlay = false;
        this.$refs["audio" + ind].pause();
        this.$refs["audio" + ind].currentTime = 0;
        if (index == ind) {
          item.isPlay = true;
          this.$refs["audio" + index].play();
        }
      });
    },
    // 停止播放
    VoicePause(index) {
      this.tableData[index].isPlay = false;
      this.$refs["audio" + index].pause();
      this.$refs["audio" + index].currentTime = 0;
    },
    // 播放完成
    onAudioEnded(index) {
      this.tableData[index].isPlay = false;
    },
    // 获取语音
    getAlarmMp3Url(id) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/algorithm/sound/stream?id=${id}&X-Token=${token}`;
    },
  },
};
</script>
<style scoped lang="scss"></style>
