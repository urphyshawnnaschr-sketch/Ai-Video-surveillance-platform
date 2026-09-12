<template>
  <div class="role-cont">
    <div class="flex-left">
      <div class="top-title">
        <div class="title">{{ $t("rolemanagement.index.o8q285") }}</div>
        <el-button
          icon="el-icon-user"
          v-if="btnData.includes('role-add')"
          @click="addFun"
          >{{ $t("rolemanagement.index.x0442u") }}</el-button
        >
      </div>
      <div class="cont">
        <div
          class="role-item"
          v-for="(item, index) in dataSource"
          :key="index"
          @click="detailFun(item.id)"
        >
          <div class="role-flex">
            <div class="role-name">
              <div>{{ item.nameCh }}</div>
              <div v-if="item.nameEn" class="mini-title">
                （{{ item.nameEn }}）
              </div>
            </div>
            <div
              class="edit-btn"
              v-if="btnData.includes('role-edit')"
              @click.stop="editFun(item.id)"
            >
              {{ $t("common.edit", { text: "" }) }}
            </div>
          </div>
          <div class="role-user">
            <span>{{ $t("rolemanagement.index.63e1t0") }}</span>
            <span style="margin-left: 20px">{{ item.accountNum }}</span>
          </div>
        </div>
      </div>
    </div>
    <div class="flex-right">
      <div class="right-cont" v-if="isShow">
        <div class="title">
          {{
            type == "add"
              ? $t("rolemanagement.index.x0442u")
              : type == "edit"
              ? $t("common.edit", { text: "" })
              : $t("casketmanagement.index.g6ttgh")
          }}
        </div>
        <AddRole
          v-if="addVisible"
          :currentId="currentId"
          :pageType="type"
          @close="close"
        />
      </div>
      <div v-else style="text-align: center">
        <img src="@/assets/images/brainstorm.png" style="width: 437px" />
        <div style="font-size: 16px">
          {{ $t("rolemanagement.index.jwm6v5") }}
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import { listData } from "./api";
import AddRole from "./components/addRole.vue";
export default {
  components: {
    AddRole,
  },
  data() {
    return {
      type: "view", //新增编辑
      isShow: false, //是否展示右侧信息
      addVisible: false,
      dataSource: [],
      currentId: "",
      btnData: [],
      btnObjList: [],
    };
  },
  created() {
    this.getBtn();
    this.getTable();
  },
  methods: {
    getBtn() {
      this.btnData = [];
      this.btnObjList = [];
      this.isDetail = false;
      const menuArr = JSON.parse(sessionStorage.getItem("menuTree"));
      let newArr = [];
      this.getbtnList(menuArr);
      this.btnObjList.filter((item, index) => {
        newArr.push(item.auth);
      });
      this.btnData = newArr;
      console.log(this.btnData);
    },
    getbtnList(data) {
      let arr = [];
      data.forEach((item) => {
        if (item.path == this.$route.path) {
          arr = item.children.filter((items, ind) => {
            return items.type == 2;
          });
          this.btnObjList = arr;
        } else {
          this.getbtnList(item.children);
        }
      });
    },
    async getTable() {
      this.loading = true;
      listData()
        .then((res) => {
          this.dataSource = res.data && res.data.length > 0 ? res.data : [];
        })
        .catch(() => {});
    },
    // 创建角色
    addFun() {
      this.addVisible = false;
      setTimeout(() => {
        this.currentId = "";
        this.isShow = true;
        this.type = "add";
        this.addVisible = true;
      }, 100);
    },
    // 编辑
    editFun(Id) {
      this.addVisible = false;
      setTimeout(() => {
        this.isShow = true;
        this.type = "edit";
        this.currentId = Id;
        this.addVisible = true;
      }, 100);
    },
    // 查看
    detailFun(Id) {
      this.addVisible = false;
      setTimeout(() => {
        this.isShow = true;
        this.type = "view";
        this.currentId = Id;
        this.addVisible = true;
      }, 100);
    },
    //
    close() {
      this.currentId = "";
      this.isShow = false;
      this.getTable();
    },
  },
};
</script>
<style scoped lang="scss">
.role-cont {
  display: flex;
  min-height: 100%;
  .title {
    font-size: 20px;
    font-weight: bold;
  }
  .flex-left {
    width: 280px;
    border-radius: 8px;
    background: #fff;
    padding: 16px;
    margin: 0px 10px 0 0;
    flex-shrink: 0;
    .top-title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 13px;
      padding: 5px 10px;
    }
    .cont {
      margin-top: 16px;
      .role-item {
        border: 1px solid #dcdcdc;
        border-radius: 6px;
        padding: 16px;
        margin-bottom: 16px;
      }
      .role-flex {
        display: flex;
        align-items: center;
        justify-content: space-between;
      }
      .role-name {
        color: rgba(0, 0, 0, 0.9);
        font-size: 16px;
        line-height: 24px;
        flex: 1;
        display: flex;
        align-items: center;
        flex-wrap: wrap;
      }
      .mini-title {
        color: rgba(0, 0, 0, 0.3);
        font-size: 13px;
      }
      .edit-btn {
        color: #E53935;
        cursor: pointer;
        font-size: 13px;
        display: none;
      }
      .role-user {
        color: rgba(0, 0, 0, 0.4);
        line-height: 22px;
        font-size: 13px;
        margin-top: 16px;
      }
      .role-item:hover {
        border: 1px solid #E53935;
        background: rgba(46, 70, 142, 0.1);
        .edit-btn {
          display: block;
        }
      }
    }
  }
  .flex-right {
    background: #fff;
    border-radius: 8px;
    padding: 16px;
    flex: 1;
  }
}
</style>
