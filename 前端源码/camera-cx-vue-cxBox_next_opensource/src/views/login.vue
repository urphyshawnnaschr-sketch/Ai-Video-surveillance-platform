<template>
  <div class="login">
    <div class="locale-picker">
      <AppLocalePicker></AppLocalePicker>
    </div>
    <div class="login-left">
      <img :src="loginLeft" class="img-log" />
    </div>
    <div class="login-right">
      <div class="cont">
        <div class="login-brand">
          <img src="@/assets/images/logo/yihcode_horizontal_dark.png" alt="YihCode" class="login-brand-logo" />
        </div>
        <h1 class="login-title">{{ $t("login.describe") }}</h1>
        <div
          class="login-card"
          v-if="!isRegister && !isForgot"
        >
          <el-form
            ref="loginForm"
            label-position="top"
            label-width="0px"
            class="login-form"
          >
            <el-form-item prop="username" :label="$t('common.accountText')">
              <el-input
                type="text"
                class="text"
                auto-complete="off"
                :placeholder="$t('login.userName')"
                v-model="params.account"
                size="medium"
              >
              </el-input>
            </el-form-item>
            <el-form-item prop="password" :label="$t('common.password', { text: '' })">
              <el-input
                :type="showPassword ? 'text' : 'password'"
                auto-complete="off"
               :placeholder="$t('common.password', { text: '' })"
                v-model="params.password"
                size="medium"
              >
                <i slot="suffix" @click="changeIconState()" class="password-toggle">
                  <img
                    v-if="showPassword"
                    src="@/assets/images/login/open.png"
                    class="open-icon"
                  />
                  <img
                    v-else
                    src="@/assets/images/login/close.png"
                    class="close-icon"
                  />
                </i>
              </el-input>
            </el-form-item>
            <el-form-item>
              <div class="remember-me">
                <el-checkbox
                  v-model="checked"
                  @change="rememberChange"
                ></el-checkbox>
                <span class="remember-text">{{ $t("login.rememberPassword") }}</span>
              </div>
            </el-form-item>
            <el-form-item>
              <el-button
                :loading="loading"
                type="primary"
                class="login-btn"
                @click.native.prevent="handleLogin"
                @keyup.enter="keyDown(e)"
              >
                <span v-if="!loading">{{
                  $t("login.loginText", { text: "" })
                }}</span>
                <span v-else>{{
                  $t("login.loginText", { text: $("login.centre") + "..." })
                }}</span>
              </el-button>
              <div class="unable-login-link">
                <el-popover
                  placement="right"
                  :title="$t('login.unLoginText')"
                  width="400"
                  trigger="hover"
                >
                  <div
                    style="font-size: 13px; line-height: 22px; color: #1A0808"
                  >
                    {{ $t("login.tripCache") }}<br />
                    {{ $t("login.tripDifferent") }} <br />
                    {{ $t("login.stepOne") }}<br />
                    {{ $t("login.stepTwo") }}<br />
                    {{ $t("login.stepThree") }}"<br />
                    {{ $t("login.stepFour") }}
                  </div>
                  <div slot="reference">{{ $t("login.unableLogin") }}</div>
                </el-popover>
              </div>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import CryptoJS from "crypto-js/crypto-js";
import {
  login,
  getRegisterSms,
  register,
  getResetPswSms,
  resetPsw,
} from "@/api/common";
import LoginLeft from "@/assets/images/login/login_left.png";
import LoginLeftEn from "@/assets/images/login/login_left_en.png";
import AppLocalePicker from "@/components/appLocalePicker";
import i18n from "../i18n";
export default {
  components: {
    AppLocalePicker,
  },
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error($t("form.verify.phoneCannot")));
      }
      // 使用正则表达式进行验证手机号码
      if (!/^1[3456789]\d{9}$/.test(value)) {
        callback(new Error($t("form.verify.phoneIncorrect")));
      }
      // 自定义校验规则 需要调用()函数！
      callback();
    };
    return {
      secretKey: "your-secret-key-123456",
      checked: false,
      activeName: "",
      isForgot: false,
      forgotForm: {
        phone: "",
        codeNum: "",
        passwordNum: "",
      },
      forgotRules: {
        phone: [{ required: true, validator: validatePhone, trigger: "blur" }],
        codeNum: [
          {
            required: true,
            message: $t("form.tip.inputVerificationCode"),
            trigger: "blur",
          },
        ],
        passwordNum: [
          {
            required: true,
            message: $t("form.tip.inputPassword"),
            trigger: "blur",
          },
        ],
      },
      isRegister: false,
      registerForm: {
        phone: "",
        codeNum: "",
        // name:'',
        passwordNum: "",
      },
      registerRules: {
        phone: [{ required: true, validator: validatePhone, trigger: "blur" }],
        codeNum: [
          {
            required: true,
            message: $t("form.tip.inputVerificationCode"),
            trigger: "blur",
          },
        ],
        // name: [
        //   { required: true, message: '请输入用户名', trigger: 'blur' }
        // ],
        passwordNum: [
          {
            required: true,
            message: $t("form.tip.inputPassword"),
            trigger: "blur",
          },
        ],
      },
      codeCd: false,
      long: 60,
      loading: false,
      currentTag: $t("login.defaultText"),
      tagList: {
        [$t("login.defaultText")]: {
          title: require("@/assets/images/login/title1.png"),
          bg: require("@/assets/images/login/bg1.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", { text: $t("login.enterprise") }),
        },
        [$t("login.wisdom", { text: $t("login.urbanManagement") })]: {
          title: require("@/assets/images/login/title2.png"),
          bg: require("@/assets/images/login/bg2.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", {
            text: $t("login.cityManagement"),
          }),
        },
        [$t("login.wisdom", { text: $t("login.traffic") })]: {
          title: require("@/assets/images/login/title3.png"),
          bg: require("@/assets/images/login/bg3.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", {
            text: $t("login.trafficManagement"),
          }),
        },
        [$t("login.wisdom", { text: $t("login.coalMine") })]: {
          title: require("@/assets/images/login/title4.png"),
          bg: require("@/assets/images/login/bg4.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", {
            text: $t("login.coalMineManagement"),
          }),
        },
        [$t("login.wisdom", { text: $t("login.energy") })]: {
          title: require("@/assets/images/login/title5.png"),
          bg: require("@/assets/images/login/bg5.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", {
            text: $t("login.energyManagement"),
          }),
        },
        [$t("login.wisdom", { text: $t("login.industry") })]: {
          title: require("@/assets/images/login/title7.png"),
          bg: require("@/assets/images/login/bg7.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", { text: $t("login.industry") }),
        },
        [$t("login.wisdom", { text: $t("login.agriculture") })]: {
          title: require("@/assets/images/login/title8.png"),
          bg: require("@/assets/images/login/bg8.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", { text: $t("login.agriculture") }),
        },
        [$t("login.wisdom", { text: $t("login.serviceStation") })]: {
          title: require("@/assets/images/login/title9.png"),
          bg: require("@/assets/images/login/bg9.png"),
          text1: $t("login.numberTransform"),
          text2: $t("login.helpUpgradeText", {
            text: $t("login.serviceStation"),
          }),
        },
      },
      params: {
        account: "",
        password: "",
      },
      appInfo: {},
      showPassword: false,
    };
  },
  created() {
    if (Cookies.get("isRe")) {
      this.checked = true;
      this.getLocal();
    } else {
      this.checked = false;
    }
  },
  mounted() {
    window.addEventListener("keydown", this.keyDown);
  },
  computed: {
    loginLeft() {
      return i18n.locale === "en" ? LoginLeftEn : LoginLeft;
    },
  },
  methods: {
    changeIconState() {
      this.showPassword = !this.showPassword;
    },
    keyDown(e) {
      // 回车则执行登录方法 键的是13
      if (e.key == "Enter") {
        this.handleLogin(); // 定义的登录方法
        e.preventDefault(); // 去掉默认的换行
      }
    },
    async handleLogin() {
      if (!this.params.account || !this.params.password) {
        return this.$message.error($t("form.verify.accountOrPwdCannot"));
      }
      let a1 = "c";
      let s2 = "$";
      let t3 = new Date().getTime();
      let p4 = CryptoJS.SHA256(this.params.password).toString(CryptoJS.enc.Hex);
      let y5 = a1 + p4 + s2 + "#" + t3;
      let obj = {
        account: this.params.account,
        password: CryptoJS.SHA256(y5).toString(CryptoJS.enc.Hex),
        t: t3,
      };
      const data = await login(obj);
      Cookies.set(data.data.tokenName, data.data.tokenValue);
      Cookies.set("sound_column_enabled", data.data.soundColumnEnable);
      Cookies.set("record_enabled", data.data.recordEnable);
      this.$store.state.cookies = data.data.tokenValue;
      this.$message.success($t("login.loginSuccess"));
      //this.$router.push("/");
      localStorage.setItem("nickname", this.params.account);
      localStorage.setItem("pw", this.params.password);
      sessionStorage.setItem("VocieSwitch", true);
      const path = sessionStorage.getItem("path");
      // if(path){
      //   this.$router.replace(path);
      // }else{
      this.$router.push("/bigScreen");
      // }
      // this.checkVersion()
    },
    handleCommand(val) {
      this.currentTag = val;
    },
    // 注册账号
    registerFun() {
      this.isRegister = true;
    },
    // 获取验证码
    async handleCaptFun() {
      this.$refs.registerForm.validateField("phone", async (valid) => {
        if (!valid) {
          // 获取验证码
          await getRegisterSms({ phone: this.registerForm.phone });
          this.startCountdown();
        }
      });
    },
    // 开始计时
    startCountdown() {
      this.codeCd = true; // 禁用按钮
      this.timer = setInterval(() => {
        this.long--;
        if (this.long === 0) {
          this.stopCountdown();
        }
      }, 1000);
    },
    //  终止计时
    stopCountdown() {
      clearInterval(this.timer); // 清除计时器
      this.codeCd = false; // 重新启用按钮
      this.long = 60; // 重置倒计时时长
    },
    // 取消注册
    resetForm(formName) {
      this.$refs[formName].resetFields();
      this.isRegister = false;
      this.stopCountdown();
    },
    // 立即注册
    submitForm(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.saveFun();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    async saveFun() {
      let obj = {
        phone: this.registerForm.phone,
        code: this.registerForm.codeNum,
        name: this.registerForm.phone,
        password: this.registerForm.passwordNum,
      };
      await register(obj);
      this.$message.success($t("login.registerSuccess"));
      this.isRegister = false;
    },
    // 忘记密码
    forgotFun() {
      this.isForgot = true;
    },
    // 重置密码-获取验证码
    async handleResetFun() {
      this.$refs.forgotForm.validateField("phone", async (valid) => {
        if (!valid) {
          // 获取验证码
          await getResetPswSms({ phone: this.forgotForm.phone });
          this.startCountdown();
        }
      });
    },
    // 取消
    cancelFun(formName) {
      this.$refs[formName].resetFields();
      this.isForgot = false;
      this.stopCountdown();
    },
    // 重置密码
    submitFun(formName) {
      this.$refs[formName].validate((valid) => {
        if (valid) {
          this.submitData();
        } else {
          console.log("error submit!!");
          return false;
        }
      });
    },
    async submitData() {
      let obj = {
        phone: this.forgotForm.phone,
        code: this.forgotForm.codeNum,
        password: this.forgotForm.passwordNum,
      };
      await resetPsw(obj);
      this.$message.success($t("login.pwdResetSuccess"));
      this.isForgot = false;
    },
    rememberChange() {
      if (this.checked === true) {
        this.setLocal();
      } else {
        this.removeLocal();
      }
    },
    // 设置
    setLocal() {
      let text = CryptoJS.AES.encrypt(
        this.params.password,
        this.secretKey
      ).toString();
      const days = "60 * 60 * 24 * 7"; // 60秒*60分*24小时*7天
      Cookies.set("loginName", this.params.account, days);
      Cookies.set("loginPassword", text, days);
      Cookies.set("isRe", this.checked);
    },
    // 读取
    getLocal() {
      if (Cookies.get("loginName")) {
        this.params.account = Cookies.get("loginName"); // 拿到账号
      }
      if (Cookies.get("loginPassword")) {
        const bytes = CryptoJS.AES.decrypt(
          Cookies.get("loginPassword"),
          this.secretKey
        );
        this.params.password = bytes.toString(CryptoJS.enc.Utf8); // 拿到密码
      }
    },
    // 清除
    removeLocal() {
      Cookies.remove("loginName");
      Cookies.remove("loginPassword");
      Cookies.remove("isRe");
    },
  },
  destroyed() {
    // 销毁事件
    window.removeEventListener("keydown", this.keyDown, false);
  },
};
</script>
<style scoped lang="scss">
.login {
  width: 100%;
  min-height: 100%;
  height: 100%;
  overflow: hidden;
  background-color: #fff;
  position: relative;
  display: flex;
  .login-left {
    height: 100vh;
  }
  .img-log {
    width: auto;
    height: 100%;
    display: inline-block;
    vertical-align: middle;
    max-width: 100%;
  }
  .login-right {
    display: flex;
    align-items: center;
    justify-content: center;
    flex: 1;
    background-color: #f5f5f5;
    background-image: 
      radial-gradient(circle at 1px 1px, rgba(0,0,0,0.02) 1px, transparent 0);
    background-size: 20px 20px;
  }
  .cont {
    width: 100%;
    max-width: px2rem(400px);
    padding: 0 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    
    .login-brand {
      width: 100%;
      display: flex;
      justify-content: center;
      margin-bottom: 18px;
    }
    .login-brand-logo {
      height: 44px;
      width: auto;
      object-fit: contain;
    }

    .login-title {
      font-size: 28px;
      font-weight: bold;
      color: #101828;;
      margin-bottom: 20px;
      text-align: center;
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
    }
    
    .login-card {
      width: 100%;
      border-radius: 14px;
      background: linear-gradient(135deg, rgba(255, 255, 255, 0.10) 0%, rgba(0, 0, 0, 0.00) 100%);
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
      padding: 40px;
    }
    
    .login-form {
      position: relative;
      width: 100% !important;
      
      ::v-deep .el-form-item__label {
        color: #101828;
        font-size: 14px;
        font-weight: normal;
        padding-bottom: 8px;
        line-height: 1.5;
      }
      
      ::v-deep .el-checkbox__inner:hover {
        border-color: #E53935;
      }
      ::v-deep .el-checkbox__input.is-checked .el-checkbox__inner,
      .el-checkbox__input.is-indeterminate .el-checkbox__inner {
        background-color: #E53935;
        border-color: #E53935;
      }
      ::v-deep .el-checkbox__input.is-focus .el-checkbox__inner {
        border-color: #E53935 !important;
      }
      
      .el-input {
        height: 36px;

        input {
          height: 36px;
        }
        ::v-deep .el-input__inner {
          height: 36px !important;
          border-radius:10px !important;
          background: rgba(255, 255, 255, 0.50);
          font-size: 14px;
          padding: 0 15px;
          border: none !important;
        }
        ::v-deep .el-input__inner:focus{
        //  border-color: #E53935 !important;
        }
        ::v-deep .el-input__inner:hover {
          // border-color: #c0c4cc;
        }
        ::v-deep .el-input__suffix{
          top: 6px;
        }
      }
      
      .password-toggle {
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        
        .close-icon {
          width: 20px;
          height: 20px;
        }
        .open-icon {
          width: 20px;
          height: 20px;
        }
      }

      .remember-me {
        display: flex;
        align-items: center;
        
        .remember-text {
          margin-left: 8px;
          color: #333;
          font-size: 14px;
        }
      }

      .login-btn {
        width: 100%;
        height: 38px !important;
        border-radius: 16px !important;
        border: 1px solid rgba(0, 0, 0, 0.00);
        background: linear-gradient(90deg, #E53935 0%, #1447E6 100%);
        box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.10), 0 4px 6px -4px rgba(0, 0, 0, 0.10);
        border: none !important;
        font-size: 16px;
        font-weight: normal;
        margin-top: 10px;
        
        &:hover {
          background: linear-gradient(90deg, #E53935 0%, #1447E6 100%);
        }
        &:active {
          background: linear-gradient(90deg, #E53935 0%, #1447E6 100%);
        }
      }
      
      .unable-login-link {
        float: right;
        text-align: right;
        margin-top: 10px;
        color: #333;
        font-size: 13px;
        cursor: pointer;
        
        &:hover {
          color: #E53935;
        }
      }
    }
  }
  .locale-picker {
    position: absolute;
    top: 20px;
    right: 18px;
  }
}
</style>
