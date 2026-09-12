<template>
  <div class="classifyDetail">
    <img
      class="bg-img"
      ref="img"
      :src="$common.handleDataset(currentImgData.id)"
    />
    <div
      v-if="currentImgData.commit.annotations && currentImgData.commit.annotations.length"
      class="tag"
      :style="{ background: currentImgData.commit.annotations[0].annotation.label[0].color }"
    >
      {{ currentImgData.commit.annotations[0].tagName }}
      <template v-if="treeToArray(currentImgData.commit.annotations[0].annotation.label)">
        - {{ treeToArray(currentImgData.commit.annotations[0].annotation.label) }}
      </template>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    currentImgData: {
      type: Object,
      default: () => {},
    },
  },
  data() {
    return {};
  },
  created() {},
  methods: {
    treeToArray(tree) {
      let name = "";
      if (!tree.length) {
        return;
      }
      if (tree[0].children.length) {
        name = this.treeToArray(tree[0].children);
      } else {
        name = tree[0].name;
      }
      return name;
    },
  },
};
</script>
<style scoped lang="scss">
.classifyDetail {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  .bg-img {
    max-width: 100%;
    max-height: 100%;
    display: block;
    position: absolute;
  }
  .tag {
    min-height: 25px;
    font-size: 12px;
    color: #fff;
    border-radius: 20px;
    padding: 3px 10px;
    position: absolute;
    left: 10px;
    top: 10px;
    display: flex;
    align-items: center;
  }
}
</style>
