<template>
  <div>
    <el-dialog
      :close-on-click-modal="false"
      :title="title"
      :visible.sync="dialogVisible"
      width="1200px"
      @close="closed"
      class="add-alg"
      top="5vh"
    >
      <div class="add-algorithm-dialog">
        <!-- 左侧：基本信息 -->
        <div class="add-left">
          <div class="panel-card">
            <div class="panel-header">
              <div class="panel-title">Basic Info</div>
              <div class="panel-subtitle">Edit algorithm basic info</div>
            </div>
            <el-form ref="form" :model="params" :rules="rules"  class="base-form">
              <el-form-item label="Algorithm Name" prop="name">
                <el-input v-model="params.name" placeholder="Please inputAlgorithm Name"></el-input>
              </el-form-item>
              <el-form-item label="AlgorithmDescription" prop="marks">
                <el-input
                  type="textarea"
                  :rows="4"
                  v-model="params.marks"
                  placeholder="Please Algorithm Description"
                ></el-input>
              </el-form-item>
              <el-form-item label="English Name" prop="englishName">
                <el-input
                  v-model="params.englishName"
                ></el-input>
              </el-form-item>
              <el-form-item label="Encoding" prop="nameEn">
                <el-input
                  v-model="params.nameEn"
                  @blur="blurCheck"
                  :class="[isExist ? 'color-red' : '']"
                  disabled
                ></el-input>
                <div
                  v-if="isExist"
                  style="color: #dd383e; font-size: 12px; position: absolute; top: 30px"
                >
                  Current name already exists，Please replace
                </div>
              </el-form-item>
              <el-form-item label="Shared Mode" prop="shareMode">
                <el-select v-model="params.shareMode" placeholder="Please Shared Mode" style="width: 100%">
                  <el-option
                    v-for="(item, index) in shareModeList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Alarm Level" prop="alarmLevelId">
                <el-select
                  v-model="params.alarmLevelId"
                  placeholder="Please selectAlarm Level"
                  style="width: 100%"
                >
                  <el-option
                    v-for="(item, index) in alarmLevelList"
                    :key="index"
                    :label="item.name"
                    :value="item.id"
                  ></el-option>
                </el-select>
              </el-form-item>
              <el-form-item label="Card">
                <el-upload
                  ref="upload"
                  class="upload-demo"
                  action=""
                  :on-change="handleChange"
                  :on-remove="handleRemove"
                  :limit="1"
                  :file-list="fileList"
                  accept=".png"
                  :auto-upload="false"
                  drag
                >
                  <!-- <el-button slot="trigger" size="small" type="primary">点击上传</el-button>
                  <div slot="tip" class="el-upload__tip"></div> -->
                  <div class="upload-content">
                    <div class="upload-icon-text">
                      <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none">
                        <path d="M12 3V15" stroke="#99A1AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M17 8L12 3L7 8" stroke="#99A1AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M21 15V19C21 19.5304 20.7893 20.0391 20.4142 20.4142C20.0391 20.7893 19.5304 21 19 21H5C4.46957 21 3.96086 20.7893 3.58579 20.4142C3.21071 20.0391 3 19.5304 3 19V15" stroke="#99A1AF" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                      <div class="upload-text">Click</div>
                    </div>
                    <div class="upload-tip">PNGFormat，325*180，≤1MB</div>
                  </div>
                </el-upload>
              </el-form-item>
              <div v-if="extraList.length > 0">
                <div class="panel-header panel-flex">
                  <div class="panel-title">Extension</div>
                  <div class="panel-tip">⚠️ Cautious</div>
                </div>
                <el-form-item
                  v-for="item in extraList"
                  :key="`extra-` + item.name"
                  :label="item.label"
                >
                  <el-input
                    v-model="params[item.name]"
                    type="text"
                    placeholder="Please inputParameter Value"
                  ></el-input>
                </el-form-item>
              </div>
            </el-form>
          </div>
        </div>

        <!-- 右侧：芯片类型与模型下载 -->
        <div class="add-right">
          <div class="panel-card">
            <div class="panel-header">
              <div class="panel-title">Chip type and model</div>
              <div class="panel-subtitle">Select and manage version files on different chips</div>
            </div>

            <div
              v-for="(item, index) in params.platforms"
              :key="index"
              class="platform-card"
            >
              <div class="platform-header">
                <div class="platform-title">
                  <i class="el-icon-cpu platform-icon"></i>
                  <span class="platform-label">Chip：{{ getPlatformLabel(item) }}</span>
                  <span class="file-count-tag">
                    {{ (dataSources[item] && dataSources[item].length) || 0 }} file(s)
                  </span>
                </div>
              </div>

              <el-table
                :data="dataSources[item]"
                border
                style="width: 100%"
                class="file-table"
              >
                <el-table-column align="left" prop="name" label="File Name"> </el-table-column>
                <el-table-column align="left" prop="size" label="File Size" width="90">
                </el-table-column>
                <el-table-column align="left" prop="source" label="File Source" width="90">
                </el-table-column>
                <el-table-column align="left" prop="md5Str" label="File MD5 Value" width="110">
                </el-table-column>
                <el-table-column align="left" label="Progress" width="220">
                  <template slot-scope="scope">
                    <el-progress
                      class="percen-sty"
                      :ref="`progress-${scope.$index}`"
                      :percentage="scope.row.process"
                      :stroke-width="6"
                    ></el-progress>
                  </template>
                </el-table-column>
                <el-table-column align="center" label="Action" width="90">
                  <template slot-scope="scope">
                    <!-- <el-button
                      type="text"
                      v-if="scope.row.isDownload && !scope.row.downloading"
                      :ref="`dlbtn-${scope.$index}`"
                      @click="downloadFile(scope.row, scope.$index)"
                      >Download</el-button
                    > -->
                    <div v-if="scope.row.isDownload && !scope.row.downloading"
                      :ref="`dlbtn-${scope.$index}`"
                      @click="downloadFile(scope.row, scope.$index)"
                      style="cursor: pointer;"
                    >
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 20 20" fill="none">
                        <path d="M10 12.5V2.5" stroke="#E53935" stroke-width="1.66667" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M17.5 12.5V15.8333C17.5 16.2754 17.3244 16.6993 17.0118 17.0118C16.6993 17.3244 16.2754 17.5 15.8333 17.5H4.16667C3.72464 17.5 3.30072 17.3244 2.98816 17.0118C2.67559 16.6993 2.5 16.2754 2.5 15.8333V12.5" stroke="#E53935" stroke-width="1.66667" stroke-linecap="round" stroke-linejoin="round"/>
                        <path d="M5.8335 8.33325L10.0002 12.4999L14.1668 8.33325" stroke="#E53935" stroke-width="1.66667" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    </div>
                    <el-button
                      type="text"
                      v-if="scope.row.isReDownload && !scope.row.downloading"
                      :ref="`dlrebtn-${scope.$index}`"
                      @click="downloadFile(scope.row, scope.$index)"
                      >Again</el-button
                    >
                    <span v-if="scope.row.downloading">Downloading...</span>
                  </template>
                </el-table-column>
              </el-table>

              <div class="tips" v-if="!ossNet">
                Currently external network，Model file will be downloaded from cloud，Please ensure network is stable
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- <div style="margin-bottom: 16px;">
          <el-button type="primary" @click="searchFun" :disabled="isExist" :icon="hasLocalFile?'el-icon-refresh':'el-icon-search'">{{ hasLocalFile?'Version Update':'Search' }}</el-button>
        </div>
        <div>
          <el-table :data="tableData" border style="width: 100%">
            <el-table-column align="center" prop="name" label="File Name">
            </el-table-column>
            <el-table-column align="center" prop="md5Str" label="MD5Verify Result" v-if="hasLocalFile">
            </el-table-column>
            <el-table-column align="center" prop="size" label="File Size">
            </el-table-column>
            <el-table-column align="center" label="Progress" width="260">
              <template slot-scope="scope">
                <el-progress
                    class="percen-sty"
                  :percentage="scope.row.process"
                ></el-progress>
              </template>
            </el-table-column>
            <el-table-column align="center" :label="$t('common.action', {text: ''})" width="80">
              <template slot-scope="scope">
                <el-button type="text" v-if="scope.row.md5Str!='Consistent'" @click="downs(scope.row.md5Str,scope.row.name,scope.$index)">{{scope.row.md5Str=='Downloading'?'Re-download':'Download'}}</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div> -->
      <span slot="footer" class="dialog-footer">
        <el-button @click="closed">{{ $t('button.cancelText', { text: '' }) }}</el-button>
        <el-button type="primary" @click="saveData" :disabled="isExist" class="save-btn">
          <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 16 16" fill="none" class="save-icon">
            <path d="M10.1333 2C10.485 2.00501 10.8205 2.14878 11.0667 2.4L13.6 4.93333C13.8512 5.17951 13.995 5.51497 14 5.86667V12.6667C14 13.0203 13.8595 13.3594 13.6095 13.6095C13.3594 13.8595 13.0203 14 12.6667 14H3.33333C2.97971 14 2.64057 13.8595 2.39052 13.6095C2.14048 13.3594 2 13.0203 2 12.6667V3.33333C2 2.97971 2.14048 2.64057 2.39052 2.39052C2.64057 2.14048 2.97971 2 3.33333 2H10.1333Z" stroke="white" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M11.3332 14.0001V9.33341C11.3332 9.1566 11.2629 8.98703 11.1379 8.86201C11.0129 8.73699 10.8433 8.66675 10.6665 8.66675H5.33317C5.15636 8.66675 4.98679 8.73699 4.86177 8.86201C4.73674 8.98703 4.6665 9.1566 4.6665 9.33341V14.0001" stroke="white" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M4.6665 2V4.66667C4.6665 4.84348 4.73674 5.01305 4.86177 5.13807C4.98679 5.2631 5.15636 5.33333 5.33317 5.33333H9.99984" stroke="white" stroke-width="1.33333" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          <span>Save</span>
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
  import {
    getTagListData,
    getListDataDetail,
    search,
    getFileOrigin,
    saveData,
    getAlarmLevelList,
    downloadCheck,
    downloadSingleFile,
    checkNameEn,
    reDownloadSingleFile,
    getFiles,
  } from '@/api/applicationMonitoring/algorithmManagement';

  export default {
    props: {
      currentId: {
        type: String,
        default: '',
      },
      hasLocalFile: {
        type: Boolean,
        default: false,
      },
    },
    data() {
      return {
        fileList: [],
        platformList: [
          {
            name: this.$t('modeltesting.addalgorithm.qqgf84'),
            id: 'chaoxing',
          },
          {
            name: this.$t('modeltesting.addalgorithm.b78692'),
            id: '1684',
          },
          {
            name: this.$t('modeltesting.addalgorithm.rdv8md'),
            id: '1684x',
          },
          {
            name: this.$t('modeltesting.addalgorithm.946l5e'),
            id: '1688',
          },
        ],
        shareModeList: [
          {
          name: 'Shared：Save resources，Suitable for tasks，such as pedestrian、Vehicle',
            id: 1,
          },
          {
          name: 'Exclusive：More stable，Suitable for precision tasks，such as continuous frames、People Flow',
            id: 0,
          },
        ],
        loading: false,
      title: this.hasLocalFile ? 'EditAlgorithm Model' : 'AddAlgorithm Model',
        dialogVisible: true,
        params: {
          id: this.currentId,
          name: '',
          nameEn: '',
          tagIds: [],
          alarmLevelId: '',
          platform: '',
          shareMode: 0,
          platforms: [],
          imageFile: '',
          englishName: '',
          marks: '',
          detail: '',
        },
        rules: {
          name: [
            {
              required: true,
              message: $t('form.tip.inputAlgorithmName'),
              trigger: 'blur',
            },
          ],
          englishName: [
            {
              required: true,
              message: $t('form.tip.inputAlgorithmEnName'),
              trigger: 'blur',
            },
          ],
          shareMode: [
            {
              required: true,
              message: this.$t('modeltesting.addalgorithm.h0em78'),
              trigger: 'change',
            },
          ],
        },
        // nameEn: [
        //     {
        //       required: true,
        //       message: $t('form.tip.inputAlgorithmEnglish'),
        //       trigger: 'blur',
        //     },
        //   ],
        tagList: [],
        tableData: [],
        alarmLevelList: [],
        listData: [],
        timerObj: null,
        isExist: false,
        // 算法文件列表, 例如： dataSources: {"chaoxing": [], "1684": [], "1684x": [], "1688": []}
        dataSources: {},
        // ossNet, 内网: true, 外网: false
        ossNet: false,
        // 模拟下载进度
        fakeTimer: null,
        // 模拟下载进度条
        fakeProgress: 0,
        // 正在下载状态
        downloading: false,
        // 扩展参数, 解析成, 数据格式 [{label: '名称', name: 'xxx', 'value': '0', element: 'input', type: 'number' }]
        // 目前只支持文本输入和数字类型
        extraList: [],
        showExtra: false,
      };
    },
    async created() {
      this.timerObj = null;
      await this.getTagListData();
      await this.listAlarmLevelData();
      if (this.currentId) {
        await this.getListDataDetail();
        // if (this.hasLocalFile) {
        //   await this.searchFun();
        // }
      }
    },
    beforeDestroy() {
      // 在组件销毁前清除定时器
      this.tableData.map((item, ind) => {
        clearInterval(item.timerObj);
        item.timerObj = '';
      });

      if (this.fakeTimer) {
        clearInterval(this.fakeTimer);
        this.fakeTimer = null;
      }
    },
    methods: {
      // 根据平台 id 获取展示名称
      getPlatformLabel(id) {
        const item = this.platformList.find((p) => p.id === id);
        return item ? item.name : id;
      },
      // 获取行业列表
      async getTagListData() {
        // const data = await getTagListData({ type: 1 });
        // this.tagList = data.data;
      },
      // 获取算法详情
      async getListDataDetail() {
        const data = await getListDataDetail({ id: this.currentId });
        Object.assign(this.params, {
          name: data.data.name,
          nameEn: data.data.nameEn,
          tagIds: data.data.tagIds,
          alarmLevelId:
            data.data.alarmLevelId && data.data.alarmLevelId != '0' ? data.data.alarmLevelId : '',
          platform: data.data.platform,
          shareMode: data.data.shareMode,
          platforms: data.data.platforms,
          englishName: data.data.englishName,
          marks: data.data.marks,
          detail: data.data.detail,
        });

        if (data.data.platforms) {
          data.data.platforms.forEach((item) => {
            this.dataSources[item] = [];
            this.fetchFiles(item);
          });
        }

        // 解析扩展参数
        if (data.data.extras && data.data.extras != '') {
          try {
            // 解析
            this.extraList = JSON.parse(data.data.extras);

            // 解析值列表
            this.extraList.forEach((item) => {
              this.$set(this.params, item.name, item.value);
            });
          } catch (e) {
            // 解析错误了
            console.log('json error', e);
          }
        }

        this.ossNet = data.data.ossNet;
      },

      // 保存算法
      saveData() {
        // 设置扩展参数的值
        let isModify = false;
        let modifyExtras = JSON.parse(JSON.stringify(this.extraList));
        if (modifyExtras.length > 0) {
          modifyExtras.forEach((item) => {
            let newVal = this.params[item.name] || '';
            let oldVal = item.value;
            if (newVal != oldVal) {
              isModify = true;
            }
            item['value'] = newVal;
          });
        }
        this.params['extras'] = JSON.stringify(modifyExtras);
        // console.log('modify extras', modifyExtras);

        this.$refs.form.validate(async (valid) => {
          if (valid) {
            const result = await this.confirmAsync();
            let formData = new FormData();

            formData.append('name', this.params.name);
            formData.append('nameEn', this.params.nameEn);
            formData.append('englishName', this.params.englishName);
            formData.append('marks', this.params.marks);
            formData.append('detail', this.params.detail);
            formData.append('tagIds', this.params.tagIds);
            formData.append('alarmLevelId', this.params.alarmLevelId);
            formData.append('platform', this.params.platform);
            formData.append('imageFile', this.params.imageFile);
            formData.append('shareMode', this.params.shareMode);
            formData.append('extras', this.params.extras || '');
            formData.append('min_detect_frame_num', this.params.min_detect_frame_num);
            formData.append('platforms', this.params.platforms);
            formData.append('id', this.params.id);

            if (isModify) {
              if (result) {
                await saveData(formData);
                this.$message.success($t('common.action', { text: $t('common.success') }));
                this.dialogVisible = false;
              } else {
                // this.$message.error('取消操作');
              }
            } else {
              await saveData(formData);
              this.$message.success($t('common.action', { text: $t('common.success') }));
              this.dialogVisible = false;
            }
          } else {
            return false;
          }
        });
      },
      // 封装 $.confirm 为 Promise
      confirmAsync(options) {
        return new Promise((resolve, reject) => {
          this.$confirm(
            this.$t('modeltesting.addalgorithm.3yj307'),
            this.$t('alarmdetail.newdetail.363ukv'),
            {
              distinguishCancelAndClose: true,
              confirmButtonText: $t('button.sureText', { text: '' }),
              cancelButtonText: $t('button.cancelText', { text: '' }),
            },
          )
            .then(() => {
              resolve(true);
            })
            .catch((e) => {
              resolve(false);
            });
        });
      },

      // 搜索
      async searchFun() {
        if (!this.params.nameEn) {
          this.$message.error($t('form.tip.inputAlgorithmEnglish'));
          return;
        }
        if (!this.params.platform) {
          this.$message.error(this.$t('addalgorithm.index.710vv4'));
          return;
        }
        if (this.tableData.length > 0) {
          this.tableData.forEach((item) => {
            if (item.timerObj) {
              clearInterval(item.timerObj);
              item.timerObj = '';
            }
          });
        }
        const obj = {
          suanfa: this.params.nameEn,
          platform: this.params.platform,
        };
        const res = await getFileOrigin(obj);
        if (res.data.length == 0) {
          this.$message(this.$t('modeltesting.addalgorithm.drei86'));
        }
        res.data.forEach((item, index) => {
          item.process = this.handleProcess(item.localLength, item.length);
          if (item.md5Str == this.$t('modeltesting.modeltesting.f3vkgb')) {
            let that = this;
            item.timerObj = setInterval(function () {
              that.inspectFun(item.name, index);
            }, 3000);
          }
        });
        this.tableData = res.data;
      },
      downs(md5Str, name, index) {
        if (md5Str == this.$t('modeltesting.modeltesting.f3vkgb')) {
          clearInterval(this.tableData[index].timerObj);
          this.tableData[index].timerObj = '';
          this.reDownload(name, index);
          return;
        }
        downloadSingleFile({
          suanfa: this.params.nameEn,
          platform: this.params.platform,
          fileName: name,
        })
          .then((res) => {
            if (res.code == 0) {
              this.inspectFun(name, index);
              let that = this;
              this.tableData[index].timerObj = setInterval(function () {
                that.inspectFun(name, index);
              }, 3000);
            }
          })
          .catch((res) => {})
          .finally((res) => {});
      },
      // 重新下载
      reDownload(name, index) {
        reDownloadSingleFile({
          suanfa: this.params.nameEn,
          platform: this.params.platform,
          fileName: name,
        })
          .then((res) => {
            if (res.code == 0) {
              this.searchFun();
            }
          })
          .catch((res) => {});
      },
      // 检查
      inspectFun(fileName, index) {
        downloadCheck({ suanfa: fileName })
          .then((res) => {
            if (res.data.msg != this.$t('modeltesting.modeltesting.f3vkgb')) {
              this.searchFun();
            } else {
              this.tableData[index].md5Str = res.data.msg;
              let tatal = this.tableData[index].length;
              this.tableData[index].process = this.handleProcess(res.data.length, tatal);
            }
          })
          .catch((res) => {
            if (
              res.msg != this.$t('modeltesting.modeltesting.f3vkgb') ||
              res != this.$t('modeltesting.modeltesting.f3vkgb')
            ) {
              this.searchFun();
            }
          });
      },
      // 关闭回调
      closed() {
        this.tableData.map((item, ind) => {
          clearInterval(item.timerObj);
          item.timerObj = '';
        });
        this.$emit('close');
      },
      // 处理进度条
      handleProcess(local, total) {
        if (local && total) {
          if (Number(local) == Number(total)) {
            return Number('100.00');
          } else {
            return ((Number(local) / Number(total)) * 100).toFixed(2) > 100
              ? Number('100.00')
              : Number(((Number(local) / Number(total)) * 100).toFixed(2));
          }
        } else {
          return 0;
        }
      },
      //
      async listAlarmLevelData() {
        const data = await getAlarmLevelList();
        this.alarmLevelList = data.data;
      },
      // 算法英文名失去焦点时，判断此算法是否存在
      blurCheck() {
        if (!this.params.nameEn) {
          this.isExist = false;
          return;
        }
        let obj = {
          id: this.currentId,
          nameEn: this.params.nameEn,
        };
        checkNameEn(obj).then((res) => {
          this.isExist = res.data;
        });
      },
      // 获取文件列表
      async fetchFiles(platform) {
        let params = {
          nameEn: this.params.nameEn,
          platform,
        };
        const result = await getFiles(params);
        if (result.code == 0) {
          this.dataSources = { ...this.dataSources, [platform]: result.data };
        }
      },
      // 下载算法文件
      downloadFile(item, index) {
        if (this.downloading) {
          this.$message.warning(this.$t('modeltesting.addalgorithm.786h2y'));
          return;
        }
        this.downloading = true;
        this.dataSources[item.platform][index].downloading = true;

        this.fakeProgress = 0;
        this.fakeTimer = setInterval(() => {
          this.fakeProgress = this.fakeProgress + 10;
          this.dataSources[item.platform][index].process = this.fakeProgress;
          if (this.fakeProgress >= 90) {
            clearInterval(this.fakeTimer);
            this.fakeTimer = null;
          }
        }, 1000);

        downloadSingleFile({
          suanfa: item.nameEn,
          platform: item.platform,
          fileName: item.name,
        })
          .then((res) => {
            clearInterval(this.fakeTimer);
            this.fakeTimer = null;
            this.fetchFiles(item.platform);
            this.downloading = false;
          })
          .catch((res) => {
            this.dataSources[item.platform][index].process = 0;
            clearInterval(this.fakeTimer);
            this.fakeTimer = null;
            this.downloading = false;
          })
          .finally((res) => {});
      },

      handleChange(file, fileList) {
        const isLimit = file.size / 1024 / 1024 < 1;
        if (!isLimit) {
          this.$message.error(this.$t('addpage.addinfo.3qhpe7'));
          this.$refs.upload.clearFiles(); // 清除前端显示的文件列表
        } else {
          let Arr = [];
          Arr.push(file);
          this.fileList = Arr;
          this.params.imageFile = file.raw;
          console.log(this.params.imageFile, 'this.params.imageFile22');
        }
      },
      handleRemove(file, fileList) {
        this.params.imageFile = '';
        this.fileList = [];
      },
    },
  };
</script>
<style lang="scss" scoped>
  .add-alg{
    :deep(){
      .el-dialog__body{
        padding: 0px !important;
      }
      .el-dialog__footer{
        border-top: 1px solid #E5E7EB;
        background: #F9FAFB;
        padding: 15px 20px;
      }
      .el-button {
        border-radius: 10px !important;
      }
      .save-btn {
        display: inline-flex !important;
        align-items: center !important;
        justify-content: center !important;
        .save-icon {
          display: inline-block;
          vertical-align: middle;
          margin-right: 6px;
          flex-shrink: 0;
        }
        span {
          display: inline-block;
          vertical-align: middle;
        }
      }
    }
  }
  .add-algorithm-dialog {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    border-top: 1px solid #E5E7EB;
    height: calc(90vh - 128px);
    overflow: hidden;
    .panel-card {
      // background: #f9fafb;
      // border-radius: 10px;
      padding: 16px 20px 20px;
      margin-bottom: 16px;
      // box-shadow: 0 1px 3px rgba(15, 23, 42, 0.06);
    }
    .panel-flex{
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .panel-header {
      margin-bottom: 10px;
      .panel-title {
        font-size: 16px;
        font-weight: 600;
        color: #111827;
      }
      .panel-subtitle {
        margin-top: 4px;
        font-size: 12px;
        color: #6b7280;
      }
      .panel-tip{
        color: #FF6900;
        font-size: 14px;
        font-style: normal;
        font-weight: 400;
        line-height: 24px; 
      }
    }
    .add-left {
      width: 320px;
      height: 100%;
      border-right: 1px solid #E5E7EB;
      background: #F9FAFB;
      overflow-y: auto;
      overflow-x: hidden;
      // padding-right: 8px;
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: #d1d5db;
        border-radius: 3px;
        &:hover {
          background: #9ca3af;
        }
      }
      .base-form {
        .el-form-item {
          margin-bottom: 16px;
        }
      }
      .extra-form {
        margin-top: 8px;
      }
      .upload-demo {
        :deep(.el-upload) {
          width: 100%;
          .el-upload-dragger {
            width: 100%;
            height: auto;
            padding: 20px;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            .upload-content {
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              gap: 8px;
              .upload-icon-text {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                gap: 8px;
                .upload-text {
                  color: #4A5565;
                  font-size: 14px;
                }
              }
              .upload-tip {
                color: #99A1AF;
                font-size: 12px;
              }
            }
          }
        }
      }
    }
    .add-right {
      flex: 1;
      height: 100%;
      margin-left: 20px;
      overflow-y: auto;
      overflow-x: hidden;
      padding-right: 8px;
      &::-webkit-scrollbar {
        width: 6px;
      }
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      &::-webkit-scrollbar-thumb {
        background: #d1d5db;
        border-radius: 3px;
        &:hover {
          background: #9ca3af;
        }
      }
      .platform-card {
        background: #ffffff;
        border-radius: 10px;
        border: 1px solid #e5e7eb;
        padding: 12px 16px 16px;
        margin-bottom: 12px;
        .platform-header {
          margin-bottom: 8px;
          .platform-title {
            display: flex;
            align-items: center;
            .platform-icon {
              margin-right: 6px;
              color: #2563eb;
            }
            .platform-label {
              font-size: 14px;
              color: #111827;
              margin-right: 10px;
            }
            .file-count-tag {
              font-size: 12px;
              color: #6b7280;
              padding: 2px 8px;
              border-radius: 10px;
              background: #eef2ff;
            }
          }
        }
        .file-table {
          .el-table__header th {
            background: #f9fafb;
          }
        }
      }
    }
  }
  .color-red {
    .el-input__inner {
      border-color: #dd383e;
    }
  }
  .percen-sty {
    .el-progress-bar {
      width: 95% !important;
      margin-right: -75px !important;
    }
    .el-progress__text {
      margin-left: 30px !important;
    }
  }
  .tips {
    font-size: 12px;
    color: #e6a23c;
    margin-top: 10px;
  }
</style>
