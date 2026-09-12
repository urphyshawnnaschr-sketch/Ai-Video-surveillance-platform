<template>
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
              <el-button icon="el-icon-plus" size="mini" @click="addFun">
                {{ $t("common.add", { text: $t("common.layer") }) }}
              </el-button>
            </div>
            <div style="margin-left: 10px">
              <el-button
                icon="el-icon-coordinate"
                size="mini"
                type="primary"
                @click="addFunMark"
                >{{ $t("mapimagemanagent.index.3173f0") }}</el-button
              >
            </div>
          </div>
        </div>
        <div class="grid-content">
          <el-tabs
            v-model="configId"
            style="max-width: 800px; width: auto"
            type="card"
            @tab-click="handleClick"
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
                :image-url="getMapImageUrl(item.cover)"
                :marker-icon-url="getMapImageUrl(currentRule.svgName)"
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
          <div style="display: flex">
            <div class="tabs">
              <!-- <div
                :class="['tabItems', { tabItemsActive: activeType == '0' }]"
                @click="activeTab('0')"
              >
                Edge Box
              </div> -->
              <div
                :class="['tabItems', { tabItemsActive: activeType == '0' }]"
                @click="activeTab('0')"
              >
                {{ $t("mapimagemanagent.index.h8zi52") }}
              </div>
              <div
                :class="['tabItems', { tabItemsActive: activeType == '1' }]"
                @click="activeTab('1')"
              >
                {{ $t("groupview.index.30617c") }}
              </div>
            </div>
          </div>
        </div>
        <div v-if="activeType == '0'">
          <el-radio-group
            v-model="groupValue"
            size="mini"
            @change="activeTab('0')"
          >
            <el-radio-button
              v-for="(item, index) in groupList"
              :key="index"
              :label="item.value"
              >{{ item.name }}</el-radio-button
            >
          </el-radio-group>
          <div style="margin-top: 16px">
            <div class="tabContent">
              <div class="tabContent-light"></div>
              {{ $t("mapimagemanagent.index.x5ks36") }}
            </div>
            <el-table
              :data="unselectList"
              style="width: 100%"
              :default-sort="{ prop: 'date', order: 'descending' }"
              height="240"
            >
              <el-table-column
                prop="objectName"
                :label="
                  $t('annotationplatform.annotationgroupmanagement.k78318')
                "
              ></el-table-column>
              <el-table-column
                prop="relName"
                :label="$t('mapimagemanagent.index.2f638u')"
              >
                <template slot-scope="scope">
                  <el-tag type="success">{{ scope.row.relName }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column
                :label="$t('mapimagemanagent.index.734ujc')"
                sortable
                width="110px"
              >
                <template slot-scope="scope">
                  <el-popover
                    placement="right"
                    width="400"
                    trigger="click"
                    @show="handleShow(scope.row.objectId)"
                  >
                    <el-table
                      :data="cameraData"
                      border
                      style="width: 100%"
                      v-loading="loading"
                    >
                      <el-table-column
                        align="center"
                        :label="$t('common.liveMap')"
                        width="90"
                      >
                        <template
                          slot-scope="scope"
                          style="padding: 0px !important"
                        >
                          <el-image
                            v-if="scope.row.fileName && scope.row.isShowImg"
                            style="width: 100%; margin-top: 5px"
                            :src="
                              $common.handleCameraImgUrl(scope.row.filename)
                            "
                            :preview-src-list="[
                              $common.handleCameraImgUrl(scope.row.filename),
                            ]"
                            @error="handleImageError(scope.$index)"
                          >
                          </el-image>
                          <img
                            v-else
                            src="@/assets/images/no-img.png"
                            style="width: 100%; margin-top: 5px"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column
                        align="center"
                        prop="name"
                        :label="$t('groupview.index.30617c')"
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        prop="boxName"
                        :label="$t('groupview.index.t8883x')"
                      ></el-table-column>
                    </el-table>
                    <el-tag
                      type="success"
                      slot="reference"
                      style="cursor: pointer"
                      >{{ scope.row.cameraCount }}</el-tag
                    >
                  </el-popover>
                </template>
              </el-table-column>
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
                prop="objectName"
                :label="
                  $t('annotationplatform.annotationgroupmanagement.k78318')
                "
              ></el-table-column>
              <el-table-column
                prop="relName"
                :label="$t('mapimagemanagent.index.2f638u')"
              >
                <template slot-scope="scope">
                  <el-tag type="success">{{ scope.row.relName }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column :label="$t('mapimagemanagent.index.734ujc')">
                <template slot-scope="scope">
                  <el-popover
                    placement="right"
                    width="400"
                    trigger="click"
                    @show="handleShow(scope.row.objectId)"
                  >
                    <el-table
                      :data="cameraData"
                      border
                      style="width: 100%"
                      v-loading="loading"
                    >
                      <el-table-column
                        align="center"
                        :label="$t('common.liveMap')"
                        width="90"
                      >
                        <template
                          slot-scope="scope"
                          style="padding: 0px !important"
                        >
                          <el-image
                            v-if="scope.row.fileName && scope.row.isShowImg"
                            style="width: 100%; margin-top: 5px"
                            :src="
                              $common.handleCameraImgUrl(scope.row.filename)
                            "
                            :preview-src-list="[
                              $common.handleCameraImgUrl(scope.row.filename),
                            ]"
                            @error="handleImageError(scope.$index)"
                          >
                          </el-image>
                          <img
                            v-else
                            src="@/assets/images/no-img.png"
                            style="width: 100%; margin-top: 5px"
                          />
                        </template>
                      </el-table-column>
                      <el-table-column
                        align="center"
                        prop="name"
                        :label="$t('groupview.index.30617c')"
                      ></el-table-column>
                      <el-table-column
                        align="center"
                        prop="boxName"
                        :label="$t('groupview.index.t8883x')"
                      ></el-table-column>
                    </el-table>
                    <el-tag
                      type="success"
                      slot="reference"
                      style="cursor: pointer"
                      >{{ scope.row.cameraCount }}</el-tag
                    >
                  </el-popover>
                </template>
              </el-table-column>
              <el-table-column prop="address" label="" width="160px">
                <template slot="header" slot-scope="scope">
                  <el-input
                    v-model="typeNamed"
                    size="mini"
                    @change="objectList()"
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
        </div>
        <div v-if="activeType == '1'">
          <div class="tabContent">
            <div class="tabContent-light"></div>
            {{ $t("mapimagemanagent.index.x5ks36") }}
          </div>
          <el-table :data="tableData" style="width: 100%" height="240">
            <el-table-column
              prop="date"
              :label="$t('components.grapdetail.8124u4')"
            >
              <template slot-scope="scope">
                <span>
                  <span v-if="activeType == '0'">
                    <i class="el-icon-cpu"></i>
                    {{ scope.row.locationName }}
                  </span>
                  <span>
                    <i class="el-icon-video-camera"></i>
                    {{ scope.row.cameraName }}
                  </span>
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="name"
              :label="$t('mapimagemanagent.index.bmdxf8')"
            >
              <template slot="header" slot-scope="scope">
                <el-popover placement="bottom" width="100" trigger="click">
                  <el-checkbox-group v-model="depIdType" @change="activeTab()">
                    <div v-for="item in depLists" :key="item.id">
                      <el-checkbox :label="item.id">{{
                        item.name
                      }}</el-checkbox>
                    </div>
                  </el-checkbox-group>
                  <span class="el-dropdown-link" slot="reference">
                    <span v-if="activeType == '0'">{{
                      $t("mapimagemanagent.index.bmdxf8")
                    }}</span>
                    <span v-if="activeType == '1'">{{
                      $t("mapimagemanagent.index.o235bx")
                    }}</span>
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                </el-popover>
              </template>
              <template slot-scope="scope">
                <el-button type="success" size="mini" plain>{{
                  activeType == "0"
                    ? scope.row.departName
                    : scope.row.locationName
                }}</el-button>
              </template>
            </el-table-column>
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
          <div class="tabContent">
            <div class="tabContent-light"></div>
            {{ $t("mapimagemanagent.index.r75v41") }}
          </div>
          <el-table :data="tableDataEd" style="width: 100%" height="240">
            <el-table-column prop="date" :label="$t('common.boxName')">
              <template slot-scope="scope">
                <span>
                  <i class="el-icon-cpu"></i>
                  {{ scope.row.objectName }}
                </span>
              </template>
            </el-table-column>
            <el-table-column
              prop="name"
              :label="$t('mapimagemanagent.index.bmdxf8')"
            >
              <template slot="header" slot-scope="scope">
                <el-popover placement="bottom" width="100" trigger="click">
                  <div>
                    <el-checkbox-group v-model="depId" @change="objectList()">
                      <el-checkbox
                        v-for="item in depLists"
                        :key="item.id"
                        :label="item.id"
                        >{{ item.name }}</el-checkbox
                      >
                    </el-checkbox-group>
                  </div>
                  <span class="el-dropdown-link" slot="reference">
                    <span v-if="activeType == '0'">{{
                      $t("mapimagemanagent.index.bmdxf8")
                    }}</span>
                    <span v-if="activeType == '1'">{{
                      $t("mapimagemanagent.index.o235bx")
                    }}</span>
                    <i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                </el-popover>
              </template>
              <template slot-scope="scope">
                <el-button type="success" size="mini" plain>{{
                  scope.row.relName
                }}</el-button>
              </template>
            </el-table-column>
            <el-table-column prop="address" label="" width="160px">
              <template slot="header" slot-scope="scope">
                <el-input
                  v-model="typeNamed"
                  size="mini"
                  @change="objectList()"
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
        <div class="bottom-btn">
          <!-- <el-button type="danger" @click="configDelete">删除图层</el-button> -->
          <div>
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
            <el-button style="margin-left: 16px" size="mini" @click="editFun">{{
              $t("common.edit", { text: $t("mapimagemanagent.index.o49st7") })
            }}</el-button>
          </div>
          <div>
            <el-button type="primary" @click="saveImage">{{
              $t("mapimagemanagent.index.0eju3m")
            }}</el-button>
          </div>
        </div>
      </el-col>
    </el-row>
    <el-dialog
      :title="$t('mapimagemanagent.index.211x24')"
      :visible.sync="dialogVisible"
      width="80%"
    >
      <div>
        <el-tabs v-model="activeType" @tab-click="activeTypeChange">
          <el-tab-pane
            :label="$t('mapimagemanagent.index.0u7mgr')"
            name="0"
          ></el-tab-pane>
          <el-tab-pane
            :label="$t('mapimagemanagent.index.qtg2v3')"
            name="1"
          ></el-tab-pane>
        </el-tabs>
        <el-table :data="formLabelAlign.rules" style="width: 100%">
          <el-table-column
            prop="date"
            :label="$t('mapimagemanagent.index.514c3o')"
          >
            <template slot-scope="scope">
              <el-input
                v-model="scope.row.name"
                :placeholder="$t('form.tip.inputContent')"
              ></el-input>
            </template>
          </el-table-column>
          <el-table-column
            prop="name"
            :label="$t('mapimagemanagent.index.i44tog')"
            width="400px"
          >
            <template slot-scope="scope">
              <el-input-number
                v-model="scope.row.min"
                @change="handleChange($event, scope.$index, 'min')"
                :min="0"
                :max="999999"
                label=""
              ></el-input-number>
              <span style="padding: 0 5px">{{
                $t("annotationplatform.statisticspage.a241tc")
              }}</span>
              <el-input-number
                v-model="scope.row.max"
                @change="handleChange($event, scope.$index, 'max')"
                :min="0"
                :max="999999"
                label=""
              ></el-input-number>
            </template>
          </el-table-column>
          <el-table-column
            prop="address"
            :label="$t('mapimagemanagent.index.m59iwi')"
          >
            <template slot-scope="scope">
              <el-color-picker v-model="scope.row.color"></el-color-picker>
            </template>
          </el-table-column>
          <el-table-column
            prop="address"
            :label="$t('common.action', { text: '' })"
          >
            <template slot-scope="scope">
              <el-button size="mini" type="danger" @click="delRules(scope)">{{
                $t("button.deleteText", { text: "" })
              }}</el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-button @click="addRules" size="mini" style="margin: 10px 0">
          + {{ $t("common.add", { text: $t("common.range") }) }}
        </el-button>
        <el-form
          :model="formLabelAlign"
          ref="ruleForm"
          label-width="140px"
          class="demo-ruleForm"
        >
          <el-form-item :label="$t('mapimagemanagent.index.196mmt')">
            <el-upload
              ref="upload"
              :http-request="handleUpload"
              :show-file-list="false"
              :limit="1"
              accept=".svg"
            >
              <el-button size="mini" type="primary">{{
                $t("button.clickToUpload")
              }}</el-button>
              <div>{{ formLabelAlign.svgName }}</div>
              <div slot="tip" class="el-upload__tip">
                {{ $t("mapimagemanagent.index.f2wt11") }}
              </div>
            </el-upload>
          </el-form-item>
          <el-form-item
            :label="$t('mapimagemanagent.index.6047su')"
            prop="name"
          >
            <!-- <el-input v-model="formLabelAlign.calcDay" placeholder="近15天"></el-input> -->
            <el-select
              v-model="formLabelAlign.calcDay"
              :placeholder="$t('mapimagemanagent.index.j9157r')"
            >
              <el-option label="00:00" :value="0"></el-option>
              <el-option label="01:00" :value="1"></el-option>
              <el-option label="02:00" :value="2"></el-option>
              <el-option label="03:00" :value="3"></el-option>
              <el-option label="04:00" :value="4"></el-option>
              <el-option label="05:00" :value="5"></el-option>
              <el-option label="06:00" :value="6"></el-option>
              <el-option label="07:00" :value="7"></el-option>
              <el-option label="08:00" :value="8"></el-option>
              <el-option label="09:00" :value="9"></el-option>
              <el-option label="10:00" :value="10"></el-option>
              <el-option label="11:00" :value="11"></el-option>
              <el-option label="12:00" :value="12"></el-option>
              <el-option label="13:00" :value="13"></el-option>
              <el-option label="14:00" :value="14"></el-option>
              <el-option label="15:00" :value="15"></el-option>
              <el-option label="16:00" :value="16"></el-option>
              <el-option label="17:00" :value="17"></el-option>
              <el-option label="18:00" :value="18"></el-option>
              <el-option label="19:00" :value="19"></el-option>
              <el-option label="20:00" :value="20"></el-option>
              <el-option label="21:00" :value="21"></el-option>
              <el-option label="22:00" :value="22"></el-option>
              <el-option label="23:00" :value="23"></el-option>
            </el-select>
            <div style="color: #1A0808; font-size: 12px">
              {{ $t("mapimagemanagent.index.42qwz7") }}
            </div>
          </el-form-item>
        </el-form>
      </div>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">{{
          $t("button.cancelText", { text: " " })
        }}</el-button>
        <el-button type="primary" @click="saveMark">{{
          $t("button.sureText", { text: " " })
        }}</el-button>
      </span>
    </el-dialog>
    <el-dialog
      :title="$t('common.prompt')"
      :visible.sync="dialogVisibleAdd"
      width="50%"
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
            <div v-if="!formLabelAlign.cover">
              <i class="el-icon-upload"></i>
              <div class="el-upload__text">
                {{ $t("modeltesting.videotest.9j6626")
                }}<em>{{ $t("button.clickToUpload") }}</em>
              </div>
            </div>
            <img
              width="100%"
              v-else
              :src="getMapImageUrl(formLabelAlign.cover)"
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
</template>
<script>
import images from "./components/images.vue";
import {
  upload,
  uploadSvg,
  configList,
  configSave,
  configDelete,
  objectList,
  objectSave,
  objectDelete,
  locationList,
  cameraList,
  ruleSave,
  ruleInfo,
  departList,
  cameraGroup,
  unselectGroup,
  groupCameraList,
} from "@/api/annotationPlatform/mapImageManagent";
import Cookies from "js-cookie";
export default {
  components: {
    images,
  },
  data() {
    return {
      groupValue: "",
      activeName: 0,
      mapList: [
        {
          id: 1,
          name: this.$t("mapimagemanagent.index.o49st7"),
        },
      ],
      tableData: [],
      tableDataEd: [],
      markersEd: [],
      dialogVisibleAdd: false,
      dialogVisible: false,
      formLabelAlign: {
        name: "",
        type: "",
        rules: [],
      },
      token: Cookies.get("X-Token"),
      VUE_APP_API_BASE_URL,
      configType: 0,
      configId: null,
      activeType: "0",
      currentRule: {},
      currentDevice: {},
      isEdit: false,
      isAdd: false,
      editIndex: 0,
      typeName: "",
      typeNamed: "",
      depLists: [
        {
          name: $t("common.allText"),
          id: 0,
        },
      ],
      depId: [],
      depIdType: [],
      groupList: [],
      unselectList: [],
      cameraData: [],
    };
  },
  created() {
    this.getConfigList();
  },
  mounted() {
    this.getDepartList();
  },
  methods: {
    getDepartList() {
      // 清空数据
      // this.depLists = [];
      // this.typeName = "";
      // this.typeNamed = "";
      // 如果当前选择的Edge Boxtab，则获取部门列表
      if (this.activeType == "0") {
        departList().then((res) => {
          this.depLists = res.data;
          this.$forceUpdate();
        });
      }

      // 如果当前选择的摄像头tab，则获取盒子列表
      if (this.activeType == "1") {
        locationList({ objectIds: [], objectName: "", objectAll: true }).then(
          (res) => {
            const data = res.data;
            let results = [];
            if (data) {
              results = data.map((item) => {
                return {
                  id: item.locationId,
                  name: item.locationName,
                };
              });
            }
            this.depLists = results;
            this.$forceUpdate();
          }
        );
      }
    },
    async handleUploadDemo(files) {
      console.info(files);
      const file = files.file;
      let form = new FormData();
      form.append("file", file);
      const res = await upload(form);
      this.$set(this.formLabelAlign, "cover", res.data);
      this.$message.success(this.$t("addproject.index.92j503"));
      this.$refs.uploadDemo.clearFiles();
    },
    async handleUpload(files) {
      console.info(files);
      const file = files.file;
      let form = new FormData();
      form.append("file", file);
      const res = await uploadSvg(form);
      this.$set(this.formLabelAlign, "svgName", res.data);
      this.$message.success(this.$t("addproject.index.92j503"));
      this.$refs.upload.clearFiles();
    },
    //切换图层
    handleClick() {
      let config = this.mapList.find((item) => item.id === this.configId);
      if (config) {
        this.configType = config.type;
      }
      this.objectList();
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
    addFunMark() {
      this.formLabelAlign = {
        rules: [] // 确保 rules 是数组
      };
      this.dialogVisible = true;
      this.ruleInfo();
    },
    saveMark(data) {
      // console.log(this.formLabelAlign, "this.formLabelAlign,");
      ruleSave({
        ...this.formLabelAlign,
        type: this.activeType,
      }).then((res) => {
        if (res.code == 0) {
          this.dialogVisible = false;
          // this.getConfigList()
          this.$message.success(
            $t("button.saveText", { text: $t("common.success") })
          );
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    async getConfigList() {
      const res = await configList();
      this.mapList = res.data;
      if (this.mapList.length > 0) {
        this.configId = this.mapList[0].id;
        this.configType = this.mapList[0].type;
        //this.objectList()
        this.$nextTick(() => {
          this.activeTab("0");
          //console.log()
          //this.$refs
        });

        //this.activeTab('0')
      }
    },
    async addConfigSave() {
      const data = {
        ...this.formLabelAlign,
        type: this.configType,
        sort: this.configType == 0 ? 1 : 2,
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
          //this.dialogVisibleAdd = false
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
    ///获取当前图层的设备图标
    objectList() {
      var d = {
        type: this.activeType,
        mapId: this.configId,
        objectName: this.typeNamed,
        objectIds: this.depId,
      };
      objectList({
        type: this.activeType,
        mapId: this.configId,
        objectName: this.typeNamed,
        objectIds: this.depId,
      }).then((res) => {
        //this.formLabelAlign.rules = res.data
        this.tableDataEd = res.data;
        this.markersEd = res.data.map((item) => {
          return {
            originX: item.position[0],
            originY: item.position[1],
            ...item,
          };
        });
      });
    },
    ///删除设备
    objectDelete(id) {
      objectDelete({
        id: id,
      }).then((res) => {
        if (res.code == 0) {
          this.objectList();
          this.$message.success(
            $t("button.deleteText", { text: $t("common.success") })
          );
        } else {
          this.$message.error(res.msg);
        }
      });
    },
    //获取未添加的图层摄像头
    async getCameraList() {
      const res = await cameraList({
        objectIds: this.depIdType,
        objectName: this.typeName,
      });
      this.tableData = res.data;
    },
    //获取未添加的图层盒子
    async getLocationList() {
      const res = await locationList({
        objectIds: this.depIdType,
        objectName: this.typeName,
      });
      this.tableData = res.data;
    },
    //切换盒子摄像头的列表
    //1：摄像头 0：盒子
    activeTab(type) {
      this.tableData = [];
      this.markersEd = [];
      this.$refs.imagesed[0].clearMarkers();
      //this.$refs.imagesed.clearMarkers()
      if (type) {
        this.activeType = type;
        this.depIdType = [];
        this.depId = [];
        this.typeName = "";
        this.typeNamed = "";
      }

      if (this.activeType == "0") {
        // this.getLocationList();
        this.getGroup();
      } else {
        this.getCameraList();
      }

      // 更新查询条件
      this.getDepartList();

      this.objectList();
      this.ruleInfo();
    },
    // 获取分组层级
    async getGroup() {
      const res = await cameraGroup();
      this.groupList = res.data;
      if (res.data && res.data.length > 0 && !this.groupValue) {
        this.groupValue = res.data[0].value;
      }
      this.getUnselectGroup();
    },
    // 获取未添加分组数据
    async getUnselectGroup() {
      const res = await unselectGroup({
        level: this.groupValue,
        objectName: this.typeName,
      });
      this.unselectList = res.data;
    },
    // 根据分组获取关联摄像头
    handleShow(id) {
      groupCameraList({ groupId: id, limit: 99, page: 1 }).then((res) => {
        this.cameraData = res.data;
        if (this.tableData.length > 0) {
          this.tableData.forEach((item) => {
            item.isShowImg = true;
          });
        }
      });
    },
    // 图片识别失败
    handleImageError(index) {
      this.tableData[index].isShowImg = false;
    },
    //添加图标规则
    addRules() {
      if (!this.formLabelAlign.rules) {
        this.$set(this.formLabelAlign, 'rules', []);
      }
      this.formLabelAlign.rules.push({
        name: "",
        min: "",
        max: "",
        color: "",
      });
    },
    //获取摄像头或者盒子的图标
    async ruleInfo() {
      const res = await ruleInfo({
        type: this.activeType,
      });
      this.currentRule = res.data;
      // 确保 rules 是数组
      if (res.data && !res.data.rules) {
        res.data.rules = [];
      }
      this.formLabelAlign = res.data || { rules: [] };
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
        data.originX,
        data.originY,
        data.x,
        data.y,
      ];
      //console.log(this.currentDevice.position,"this.currentDevice.position")
    },
    async saveImage() {
      let objectId = this.currentDevice.objectId;

      if (this.isAdd) {
        objectId =
          this.activeType == "0"
            ? this.currentDevice.objectId
            : this.currentDevice.cameraId;
      }
      let data = {
        id: this.currentDevice.id,
        mapId: this.configId,
        objectId: objectId,
        type: this.activeType,
        position: this.currentDevice.position,
      };
      if (!data.objectId) {
        this.$message.error(this.$t("mapimagemanagent.index.73w28m"));
        return;
      }

      if (!data.position || data.position.length < 4) {
        this.$message.error(this.$t("mapimagemanagent.index.p3xe4l"));
        return;
      }
      const res = await objectSave(data);
      if (res.code === 0) {
        this.isAdd = false;
        this.isEdit = false;
        this.objectList();
        this.activeTab();
        this.$message({
          type: "success",
          message: $t("button.saveText", { text: $t("common.success") }),
        });
      }
    },
    activeTypeChange() {
      this.ruleInfo();
    },
    handleChange(value, index, key) {
      this.formLabelAlign.rules[index][key] = value;

      this.$forceUpdate();
    },
    // 切换层级
    groupChange() {},
    // 图片地址
    getMapImageUrl(filename) {
      const token = Cookies.get('X-Token');
      return `${VUE_APP_API_BASE_URL}/map/file/stream?filename=${filename}&X-Token=${token}`;
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
    justify-content: space-between;
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
