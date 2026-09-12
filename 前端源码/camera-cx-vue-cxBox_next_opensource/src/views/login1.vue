<template>
  <div
    class="login"
    :style="{ 'background-image': `url(${tagList[currentTag].bg}` }"
  >
    <div class="tags">
      <el-dropdown @command="handleCommand">
        <el-button>
          {{ currentTag }}<i class="el-icon-arrow-down el-icon--right"></i>
        </el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item :command="$t('login.defaultText')">{{
            $t("login.defaultText")
          }}</el-dropdown-item>
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.urbanManagement') })"
            >{{
              $t("login.wisdom", { text: $t("login.urbanManagement") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.traffic') })"
            >{{
              $t("login.wisdom", { text: $t("login.traffic") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.coalMine') })"
            >{{
              $t("login.wisdom", { text: $t("login.coalMine") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.energy') })"
            >{{
              $t("login.wisdom", { text: $t("login.energy") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.park') })"
            >{{
              $t("login.wisdom", { text: $t("login.park") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.industry') })"
            >{{
              $t("login.wisdom", { text: $t("login.industry") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.agriculture') })"
            >{{
              $t("login.wisdom", { text: $t("login.agriculture") })
            }}</el-dropdown-item
          >
          <el-dropdown-item
            :command="$t('login.wisdom', { text: $t('login.serviceStation') })"
            >{{
              $t("login.wisdom", { text: $t("login.serviceStation") })
            }}</el-dropdown-item
          >
        </el-dropdown-menu>
      </el-dropdown>
    </div>
    <div class="login-content">
      <div class="login-center">
        <div class="login-left">
          <img :src="tagList[currentTag].title" />
          <div v-if="!isRegister && !isForgot">
            <el-form
              ref="loginForm"
              label-position="left"
              label-width="0px"
              class="login-form"
            >
              <el-form-item prop="username">
                <el-input
                  type="text"
                  auto-complete="off"
                  :placeholder="$t('login.userName')"
                  v-model="params.account"
                  size="-"
                >
                  <i slot="prefix" class="el-input__icon el-icon-user" />
                </el-input>
              </el-form-item>
              <el-form-item prop="password">
                <el-input
                  type="password"
                  show-password
                  auto-complete="off"
                  :placeholder="$t('common.password', { text: '' })"
                  v-model="params.password"
                  size="-"
                >
                  <i slot="prefix" class="el-input__icon el-icon-lock" />
                </el-input>
              </el-form-item>
              <el-form-item>
                <el-button
                  :loading="loading"
                  type="primary"
                  style="width: 100%; height: 40px !important"
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
                <div class="bottom-btn">
                  <div @click="registerFun">
                    {{ $t("login.registerAccount") }}
                  </div>
                  <div @click="forgotFun">
                    {{ $t("common.password", { text: $t("login.forget") }) }}?
                  </div>
                </div>
              </el-form-item>
            </el-form>
          </div>
          <!-- 注册账号 -->
          <div v-if="isRegister" style="margin-top: 20px">
            <el-form
              :model="registerForm"
              :rules="registerRules"
              ref="registerForm"
              label-position="left"
              label-width="80px"
            >
              <el-form-item :label="$t('login.phoneNumber')" prop="phone">
                <el-input
                  type="text"
                  :placeholder="$t('form.tip.inputPhoneNumber')"
                  v-model="registerForm.phone"
                >
                </el-input>
              </el-form-item>
              <el-form-item
                :label="$t('login.verificationCode')"
                prop="codeNum"
              >
                <el-input
                  type="text"
                  :placeholder="$t('form.tip.inputVerificationCode')"
                  v-model="registerForm.codeNum"
                >
                  <el-button
                    :disabled="codeCd"
                    size="mini"
                    slot="append"
                    style="
                      border-radius: unset !important;
                      height: 30px !important;
                      margin: 0px -20px !important;
                      background: #e5e7ea !important;
                    "
                    @click="handleCaptFun()"
                    >{{ $t("login.obtainVerificationCode") }}
                    <span v-if="codeCd">({{ long }})</span>
                  </el-button>
                </el-input>
              </el-form-item>
              <!-- <el-form-item label="用户名" prop="name">
                <el-input
                  type="text"
                  placeholder="Please input username"
                  v-model="registerForm.name"
                >
                </el-input>
              </el-form-item> -->
              <el-form-item
                :label="$t('common.password', { text: '' })"
                prop="passwordNum"
              >
                <el-input
                  type="password"
                  show-password
                  auto-complete="off"
                  :placeholder="$t('common.password', { text: '' })"
                  v-model="registerForm.passwordNum"
                >
                </el-input>
              </el-form-item>
              <el-form-item>
                <div style="text-align: right">
                  <el-button @click="resetForm('registerForm')">{{
                    $t("button.cancelText", { text: "" })
                  }}</el-button>
                  <el-button type="primary" @click="submitForm('registerForm')"
                    >{{ $t("login.registerNow") }}
                  </el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
          <!-- 忘记密码 -->
          <div v-if="isForgot" style="margin-top: 20px">
            <el-form
              :model="forgotForm"
              :rules="forgotRules"
              ref="forgotForm"
              label-position="left"
              label-width="80px"
            >
              <el-form-item :label="$t('login.phoneNumber')" prop="phone">
                <el-input
                  type="text"
                  :placeholder="$t('form.tip.inputPhoneNumber')"
                  v-model="forgotForm.phone"
                >
                </el-input>
              </el-form-item>
              <el-form-item
                :label="$t('login.verificationCode')"
                prop="codeNum"
              >
                <el-input
                  type="text"
                  :placeholder="$t('form.tip.inputVerificationCode')"
                  v-model="forgotForm.codeNum"
                >
                  <el-button
                    :disabled="codeCd"
                    size="mini"
                    slot="append"
                    style="
                      border-radius: unset !important;
                      height: 30px !important;
                      margin: 0px -20px !important;
                      background: #e5e7ea !important;
                    "
                    @click="handleResetFun()"
                    >{{ $t("login.obtainVerificationCode") }}
                    <span v-if="codeCd">({{ long }})</span>
                  </el-button>
                </el-input>
              </el-form-item>
              <el-form-item
                :label="$t('button.resetText', { text: $t('common.password') })"
                prop="passwordNum"
              >
                <el-input
                  type="password"
                  show-password
                  auto-complete="off"
                  :placeholder="$t('common.password', { text: '' })"
                  v-model="forgotForm.passwordNum"
                >
                </el-input>
              </el-form-item>
              <el-form-item>
                <div style="text-align: right">
                  <el-button @click="cancelFun('forgotForm')">{{
                    $t("button.cancelText", { text: "" })
                  }}</el-button>
                  <el-button type="primary" @click="submitFun('forgotForm')">{{
                    $t("button.resetText", { text: $t("common.password") })
                  }}</el-button>
                </div>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <div class="login-right">
          <div>
            <p>{{ tagList[currentTag].text1 }}</p>
            <p>{{ tagList[currentTag].text2 }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Cookies from "js-cookie";
import {
  login,
  getRegisterSms,
  register,
  getResetPswSms,
  resetPsw,
} from "@/api/common";
export default {
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
    };
  },
  created() {},
  mounted() {
    window.addEventListener("keydown", this.keyDown);
  },
  methods: {
    keyDown(e) {
      // 回车则执行登录方法 键的是13
      if (e.key == "Enter") {
        this.handleLogin(); // 定义的登录方法
        e.preventDefault(); // 去掉默认的换行
      }
    },
    async handleLogin() {
      if (!this.params.account || !this.params.password) {
        return this.$message.error(this.$t("views.login.c22e32"));
      }
      const data = await login(this.params);
      Cookies.set(data.data.tokenName, data.data.tokenValue);
      this.$store.state.cookies = data.data.tokenValue;
      this.$message.success($t("login.loginSuccess"));
      //this.$router.push("/");
      localStorage.setItem("nickname", this.params.account);
      localStorage.setItem("pw", this.params.password);
      const path = sessionStorage.getItem("path");
      if (path) {
        this.$router.replace(path);
      } else {
        this.$router.push("/bigScreen");
      }
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
  },
  destroyed() {
    // 销毁事件
    window.removeEventListener("keydown", this.keyDown, false);
  },
};
</script>
<style scoped lang="scss">
.login {
  position: relative;

  overflow: hidden;

  width: 100vw;
  height: 100vh;

  background: url("~@/assets/images/login/bg1.png") no-repeat center center;
  background-size: cover;

  .tags {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 2;
  }
  .logo {
    position: relative;

    display: flex;
    align-items: center;

    height: 9%;
    padding-left: 40px;

    color: #E53935;

    font-size: 32px;
    font-weight: bold;
    line-height: 9%;

    & > img {
      margin-right: 10px;
    }
    .title {
      height: 30px;
      margin-left: 10px;
      padding-left: 20px;

      border-left: 2px solid #E53935;

      line-height: 30px;
    }
  }
  .login-content {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    .login-left {
      display: flex;
      align-content: center;
      justify-content: center;
      flex-wrap: wrap;
      width: 453px;
      height: 682px;
      border-radius: 8px 0px 0px 8px;
      background: #fff;
      .login-form {
        position: relative;
        width: 400px !important;
        margin-top: 60px;
        .title {
          height: 20px;
          margin-bottom: 60px;
          text-align: center;
          color: #061640;
          font-size: 20px;
          font-weight: 500;
          line-height: 30px;
        }
        .title::after {
          position: absolute;
          top: 60px;
          left: 184px;
          width: 40px;
          height: 2px;
          content: "";
          border-radius: 2px;
          background: #E53935;
        }
        .el-input {
          height: 42px;

          input {
            height: 42px;
          }
          & >>> .el-input__inner {
            width: 400px;
            height: 42px !important;

            border: 0px !important;
            border-bottom: 1px solid rgba($color: #061640, $alpha: 0.2) !important;
            border-radius: 0px !important;
          }
        }

        .input-icon {
          width: 14px !important;
          height: 49px !important;
          margin-left: 2px;
        }
        .login-btn {
          width: 400px !important;
          height: 40px !important;
          margin-top: 20px !important;

          color: #ffffff;
          background: #E53935 !important;

          font-size: 18px !important;
          font-weight: 500 !important;
        }
        .register {
          margin-top: 20px !important;

          text-align: center !important;

          font-size: 13px !important;
          .thereIsNoAccount {
            color: #061640 !important;
          }
          .freeSignup {
            cursor: pointer !important;

            color: #E53935 !important;
          }
        }
      }
      .login-code {
        float: right !important;

        width: 33%;
        height: 38px;

        img {
          width: 140px;
          height: 100%;

          cursor: pointer;
          vertical-align: middle;
        }
      }
    }
    .login-center {
      display: flex;
      width: 1084px;
      height: 682px;
      .login-right {
        width: 631px;
        height: 682px;
        color: #fff;
        font-size: 24px;
        font-weight: bold;
        letter-spacing: 12px;
        text-align: center;
        border-radius: 0px 8px 8px 0px;
        background: url("~@/assets/images/login/imgRight.png") no-repeat center
          center;
        background-size: 100% 100%;
        display: flex;
        align-items: center;
        justify-content: center;
        .title {
          height: 45px;
          margin: 56px 0px 20px 40px;

          color: #ffffff;

          font-size: 32px;
          font-weight: 600;
          line-height: 45px;
        }
        .slogan {
          height: 28px;
          margin-left: 40px;

          color: #ffffff;

          font-size: 20px;
          font-weight: 600;
          line-height: 28px;
        }
        .aiImg {
          display: flex;
          justify-content: center;

          width: 508px;
          img {
            width: 508px;
            height: 596px;
            display: block;
            object-fit: cover;
          }
        }
      }
    }
  }
  .copyright {
    position: absolute;
    bottom: 17px;

    width: 100%;

    text-align: center;

    color: #ffffff;

    font-size: 12px;
    font-weight: 400;
  }
}
.loginCode {
  display: flex !important;
  justify-content: space-between !important;
  .el-input {
    width: 63% !important;
    height: 42px;

    input {
      height: 42px;
    }
    & >>> .el-input__inner {
      width: 270px !important;
    }
  }
}

.bottom-btn {
  color: #E53935;
  color: #E53935;
  display: flex;
  justify-content: space-between;
  padding-top: 15px;
  cursor: pointer;
  font-size: 12px;
}
</style>
