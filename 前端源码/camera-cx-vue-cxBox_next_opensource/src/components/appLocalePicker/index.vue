<template>
  <el-dropdown placement="bottom" trigger="click" @command="handleCommand">
    <img src="@/assets/images/locale-icon.png" class="locale-icon" />
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item
        v-for="item in localeList"
        :key="item.value"
        :command="item.value"
        :class="{ selected: selectedKey == item.value }"
        >{{ item.text }}</el-dropdown-item
      >
    </el-dropdown-menu>
  </el-dropdown>
</template>
<script>
export default {
  name: "AppLocalePicker",

  data() {
    return {
      localeList: [
        { text: "SimplifiedChinese", value: "zh-CN" },
        { text: "English", value: "en" },
      ],
      selectedKey: this.$store.state.locale,
    };
  },
  methods: {
    handleCommand(key) {
      if (this.selectedKey === key) {
        return;
      }
      this.selectedKey = key;
      this.toggleLocale(key);
    },
    toggleLocale(key) {
      this.$store.commit("setLocale", key);
      location.reload();
    },
  },
};
</script>
<style lang="scss">
.locale-icon {
  width: 22px;
  display: block;
  cursor: pointer;
}
.selected {
  color: #E53935;
}
</style>
