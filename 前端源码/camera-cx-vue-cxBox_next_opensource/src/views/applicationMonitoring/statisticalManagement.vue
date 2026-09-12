<template>
  <div>
    <iframe class="iframe" scrolling="no" :src="url"></iframe>
  </div>
</template>
<script>


export default {
  data() {
    return {
      url: '/#/bigScreen'
    };
  },
  created() {

  },
  methods: {},
};
</script>
<style scoped lang="scss">
.iframe {
  width: 100%;
  height: calc(100vh - 130px);
  border: medium none;
}
</style>
