<template>
    <div class="title-warp">
        <div class="title-items">
            <div class="border-left"></div>
            <div class="title-font">{{name}}</div>
        </div>
        <div class="warn-msg" v-show="isShow"><span>{{$t('components.title.90768y')}}{{type}}{{$t('components.title.v0w0h5')}}</span><span class="num-style">{{num}}{{$t('components.title.4o90f1')}}</span></div>
    </div>
</template>
<script>
import i18n from '@/i18n'
    export default {
        name: 'titleItems',
        components: {},
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
        data() {
            return {}
        },
        methods: {}
    }
</script>
<style lang="scss" scoped>
    .title-warp{
        display: flex;
        justify-content: space-between;
        align-items: center;
        background-image: linear-gradient(to right, #1d599a , #050a0b00);
        height: 60px;
        margin-bottom: 10px;
    }
    .title-font{
        margin-left: 10px;
        color: white;
        letter-spacing: 10px;
        font-size: 18px;
    }
    .park-first-right{
        width: 70%;
    }
    .title-items{
        display: flex;
        width: 100%;
        height:50px;
        align-items: center;
    }
    .border-left{
        height: 25px ;
        border-left: 4px solid #06daf4;
    }
    .warn-msg{
        width: 290px;
        height: 48px;
        background: url('../assets/img/warningBg.png');
        color: #fff;
        display: flex;
        align-items: center;
        padding-left: 42px;

    }
    .num-style{
        margin: 0 5px;
        font-size: 20px;
    }
</style>
