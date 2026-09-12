<template>
  <div class="title">
    <div :class="{'title810': isShow, 'title510': !isShow}">
        {{ name }}
    </div>
    <div class="extra">
      <slot></slot>
    </div>
  </div>
</template>

<script>
import i18n from '@/i18n'
export default {
    props: {
        name: {
            type: String,
            default: ''
        },
        type: {
            type: String,
            default: () => i18n.t('bigscreen.title.g9g2jw')
        },
        num: {
            type: Number,
            default: 0
        },
        isShow: {
            type: Boolean,
            default: false
        }
    },
}
</script>

<style lang="scss" scoped>
.title {
  position: relative;
    .title810 {
        width: 810px;
        height: 42px;
        background: url('@/assets/images/title810.png') no-repeat;
        font-size: 18px;
        line-height: 42px;
        text-indent: 12px;
        font-weight: bold;
        color: #fff;
        font-style: oblique;
        letter-spacing:3px;
    }
    .title510 {
        width: 510px;
        height: 42px;
        background: url('@/assets/images/titile510.png') no-repeat;
        font-size: 18px;
        line-height: 42px;
        text-indent: 12px;
        font-weight: bold;
        color: #fff;
        font-style: oblique;
        letter-spacing:3px;
    }

  .extra {
    position: absolute;
    right: 10px;
    top: 10px;
  }
}

</style>