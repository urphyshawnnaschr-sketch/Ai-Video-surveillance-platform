<template><div><template v-for="(item, index) in data"><!--Because for has child Set and no child Set 渲 染 Label not One sample, The with need part for Two kind Situation Situation One: has child Set Situation:--><el-submenu :key="index" :index="item.path" v-if="item.children&&item.children.length>0"><template slot="title"><template v-if="isImageIcon(item.icon)"><img 
              :src="getImageSrc(item.icon,false)" 
              class="menu-icon-img"
            /></template>
          <i v-else :class="item.icon"></i>
          <span class="menu-text">{{ item.name }}</span>
        </template>
        <myitem :data="item.children"></myitem>
      </el-submenu>
      <!-- 情况二：没子集的情况： -->
      <el-menu-item
        :key="item.path"
        v-if="(!item.children || item.children.length === 0) && !item.meta?.hideMenu"
        :index="item.path"
        :class="{ 'is-active': isItemActive(item.path) }"
      >
        <template v-if="isImageIcon(item.icon)">
          <img 
            :src="getImageSrc(item.icon, isItemActive(item.path))" 
            class="menu-icon-img"
          />
        </template>
        <i v-else :class="item.icon"></i>
        <span slot="title" class="menu-text">{{ item.name }}</span>
      </el-menu-item>
    </template>
  </div>
</template>

<script>
export default {
  name: "myitem",
  props: {
    data: {
      type: Array,
      default: [],
    },
  },
  methods: {
    // 判断是否是图片图标
    isImageIcon(icon) {
      return icon && icon.includes('.png');
    },
    // 获取图片路径
    getImageSrc(icon, isActive) {
      if (isActive) {
        return require(`@/assets/images/menu-icon/select-${icon}`);
      } else {
        return require(`@/assets/images/menu-icon/${icon}`);
      }
    },
    // 判断菜单项是否激活
    isItemActive(path) {
      if (!path) return false;
      const currentPath = this.$route.path;
      // 精确匹配或路径前缀匹配
      return currentPath === path || currentPath.startsWith(path + '/');
    },
    // 判断子菜单是否激活（检查是否有子项被激活）
    isSubmenuActive(item) {
      if (!item || !item.children) return false;
      // 检查子菜单中是否有项被激活
      return item.children.some(child => {
        if (child.children && child.children.length > 0) {
          return this.isSubmenuActive(child);
        }
        return this.isItemActive(child.path);
      });
    },
  },
  // 注意： 在标签上使用v-for，:key="index"不能写在标签上，因为其标签不会被渲染，会引起循环错误
  mounted(){
    const menuArr = JSON.parse(sessionStorage.getItem('menuTree'));
      let pathUrl = sessionStorage.getItem('path');
    if(pathUrl&&pathUrl!='/'){
      this.$router.push({ path:pathUrl})
    }else{
      if(menuArr&&menuArr.length>0){
        if(menuArr[0].type==0){
          let path = menuArr[0].children[0].path
          this.$router.push({ path:path})
        }else if(menuArr[0].type==1){
          let path = menuArr[0].path
          this.$router.push({ path:path})
        }
      }
    }
  }
};
</script>

<style scoped lang="scss">
 .menu-text {
    // 文本过长时显示省略号
    text-overflow: ellipsis;
    overflow: hidden;
    display: inline-block;
    vertical-align: middle;
  }

:deep(.el-tooltip) {
  width: 70px !important;
}

.menu-icon-img {
  width: 16px;
  // height: 18px;
  margin-right: 12px;
  vertical-align: middle;
  object-fit: contain;
}
</style>
