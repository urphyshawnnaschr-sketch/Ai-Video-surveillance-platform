<template>
  <div class="container">
    <el-container>
      <el-header class="header-flex">
        <div class="logo">
          <img src="@/assets/images/menu-icon/zoom-icon.png" style="width: 24px; height: 24px;cursor: pointer;" @click="isCollapse = !isCollapse"/>
          <img
            v-if="!imageError"
            src="@/assets/images/logo/yihcode_horizontal_dark.png"
            class="app-logo"
            @error="handleImageError"
            />
            <div class="title-txt">{{ this.$store.state.appInfo.appName }}</div>
        </div>
        <div class="userInfo">
          <AppLocalePicker></AppLocalePicker>
          <el-image
            style="width: 30px; height: 30px; border-radius: 50%;margin-left: 20px;"
            :src="avatar"
            fit="cover"
          ></el-image>
          <el-dropdown @command="handleCommand">
            <span class="userName" style="font-size: 20px; color: #202b3d"
              >{{ userInfo.nickname
              }}<i class="el-icon-arrow-down el-icon--right"></i
            ></span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="a">{{
                $t("layout.index.465ws2")
              }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
          <i class="el-icon-s-unfold" @click="changePath"></i>
        </div>
      </el-header>
      <el-container class="content-container">
        <el-aside width="224px" v-if="!isCollapse" class="menu-sty">
          <el-menu
            :default-active="activeIndex"
            class="elMenu"
            text-color="#364153"
            :unique-opened="true"
            router
            ref="elMenu"
            :collapse="isCollapse"
          >
            <myitem :data="menuArr" />
          </el-menu>
        </el-aside>
        <el-main class="main-scroll">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import Cookies from "js-cookie";
import myitem from "./components/myitem.vue";
import allRoutes from "@/router/data";
import Axios from "axios";
import { handlePublicUrl } from "@/utils/common";
import { getLastFileOrigin } from "@/api/common";
import NewVersion from "@/components/versionInfo/newVersion.vue";
import HistoricalVersion from "@/components/versionInfo/historicalVersion.vue";
import AppLocalePicker from "@/components/appLocalePicker";
import store from '@/store'

export default {
  name: "LayoutIndex",
  components: {
    myitem,
    NewVersion,
    HistoricalVersion,
    AppLocalePicker,
  },
  watch: {
    $route: {
      immediate: true,
      deep: true,
      handler(to) {
        let x = to.path.indexOf("/");
        for (let i = 0; i < 2; i++) {
          x = to.path.indexOf("/", x + 1);
        }
        if (x > -1) {
          this.activeIndex = to.path.substring(0, x);
        } else {
          if (to.name == this.$t("layout.index.igj87e")) {
            this.activeIndex = "/algorithmManagement/modelTesting";
          } else {
            this.activeIndex = to.path;
          }
        }
        sessionStorage.setItem("path", to.path);
      },
    },
  },
  data() {
    return {
      isCollapse: false,
      VUE_APP_API_BASE_URL,
      activeIndex: this.$route.path,
      menuArr: [], //allRoutes[0].children,
      userInfo: {
        nickname: localStorage.getItem("nickname")
          ? localStorage.getItem("nickname")
          : "admin",
        password: localStorage.getItem("pw")
          ? localStorage.getItem("pw")
          : "66$",
      },
      avatar: require("@/assets/images/man.png"),
      isImg: false,
      versionObj: {},
      isNew: true,
      isNewVersion: false,
      isHistorical: false,
      version: "1.4.0",
      versionNum: "1.4.0",
      imageError: false,
      logoUrl:'',
    };
  },
  created() {
    this.int();
    // this.getV();
    // this.handleBreadcrumb(this.$route);
  },
  methods: {
    int() {
      let menuArr = JSON.parse(JSON.stringify(allRoutes[0].children));
      if (sessionStorage.getItem("menu")) {
        this.menuArr = [
          ...menuArr,
          ...JSON.parse(sessionStorage.getItem("menu")),
        ];
      } else {
        this.menuArr = menuArr;
      }
      this.logoUrl = `${VUE_APP_API_BASE_URL}/config/upload/stream?file=${this.$store.state.appInfo.logoUrl}&t=${new Date().getTime()}`;
    },
  isEnglish() {
  const getLanguage = ()=>{
  const langMap = {
    'zh-CN': 'zh-CN',
    'en': 'en-US'
  }
  return langMap[store.state.locale] || 'zh-CN'
}
  return getLanguage().startsWith('en');
},
    handleImageError() {
      this.imageError = true;
    },
    async getV() {
      const res = await getLastFileOrigin();
      this.versionObj = res.data;
      this.versionNum = res.data.version;
      // if(!this.version){
      //   this.versionNum = res.data.version;
      //   this.isNew = true;
      // }else{
      //   if(this.version==res.data.version){
      //     this.versionNum = res.data.version;
      //     this.isNew = false;
      //   }else{
      //     this.versionNum = this.version;
      //     this.isNew = true;
      //   }
      // }
    },
    // 鼠标悬浮
    onHover() {
      this.isImg = true;
    },
    // 鼠标离开
    hideMessage() {
      this.isImg = false;
    },
    changePath() {
      sessionStorage.removeItem("path");
      this.$router.push("/bigScreen");
      setTimeout(function () {
        window.location.reload();
      }, 90);
    },
    goBack() {
      window.history.go(-1);
    },
    handleCommand() {
      // 退出登录
      Axios({
        url: handlePublicUrl("/logout"),
        method: "get",
        headers: {
          "X-Token": Cookies.get("X-Token"),
        },
      });

      // 清理缓存
      this.$store.state.userInfo = this.$store.state.menu = null;
      this.$store.state.cookies = "";
      Cookies.remove("X-Token");
      localStorage.removeItem("nickname");
      localStorage.removeItem("pw");
      sessionStorage.removeItem("VocieSwitch");
      sessionStorage.removeItem("menu");
      sessionStorage.removeItem("menuTree");
      sessionStorage.removeItem("path");
      this.$store.state.routerNav = [];
      this.$router.push("/login");
    },
    downFile() {
      // Axios({
      //   url:handlePublicUrl('/account/download?fileName=使用说明.txt'),

      // })

      Axios({
        url: handlePublicUrl(this.$t("layout.index.2771fs")),
        method: "get", //默认方法，可以不写
        responseType: "blob", //或者是
        headers: {
          "X-Token": Cookies.get("X-Token"),
          "Access-Control-Allow-Origin": "*",
          "Access-Control-Allow-Methods": "GET, POST, PUT, DELETE, OPTIONS",
          "Access-Control-Allow-Headers": "Content-Type, Authorization",
        },
      }).then((res) => {
        const url = window.URL.createObjectURL(new Blob([res.data]));
        const link = document.createElement("a");
        link.href = url;
        link.setAttribute("download", this.$t("layout.index.8zqw8v")); // 设置下载文件的名称和扩展名
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
      });
    },
    // 查看最新版本
    versionFun() {
      this.title =
        this.$t("layout.index.1ij932") +
        this.versionObj.version +
        this.$t("layout.index.d412oq");
      this.isNewVersion = true;
    },
    // 关闭最新版本
    closeNewVersion() {
      this.isNewVersion = false;
    },
    // 查看历史版本
    historicalFun() {
      this.isHistorical = true;
    },
    closeHistory() {
      this.isHistorical = false;
    },
  },
};
</script>
<style scoped  lang="scss">
.container {
  height: 100vh;
  .header-flex{
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #E5E7EB;
    height: 64px;
  }
  .logo{
    display: flex;
    align-items: center;
    gap: 16px;
    img{
      height: 32px;
    }
    .title-txt{
      font-size: 20px;
      color: #1E2939;
      line-height: 28px;
    }
  }
  .userInfo {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    .el-icon-s-unfold {
      color: #364153;
      font-size: 20px;
      cursor: pointer;
    }
    .userName {
      color: #364153;
      padding: 0 16px;
      cursor: pointer;
    }
  }
  .content-container{
    height: calc(100vh - 64px);
    overflow: hidden;
    
  }
  .menu-sty{
    border-right: 1px solid #E5E7EB;
    height: 100%;
    padding: 12px;
  }
  .main-scroll{
    background: #F8FAFC;
    height: 100%;
    overflow-y: auto;
    padding: 16px;
    box-sizing: border-box;
  }
}
</style>