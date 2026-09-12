<template>
    <div class="wrap-container sn-container">
        <div class="sn-content">
            <div class="sn-body">
                <div class="wrap-container">
                    <div class="table">
                        <div @click="handleButton($event)">
                            <vue-seamless-scroll :data='listData' class="seamless-warp" :class-option="optionSetting">
                                <!-- <div class="alarmPicture-wrap" v-for='(item, i) in listData' :key='i'>
                                    <div class="picture-style">
                                        <img style="width: 100%" :src="item.webUrl + '/report/stream?id=' + item.id" alt="">
                                    </div>
                                    <div class="alarm-msg">
                                        <div class="msgLine">Alarm Time：{{item.alarmTime}}</div>
                                        <div class="msgLine">Location：{{item.cameraName}}</div>
                                        <div class="msgLine">Event Type：{{item.algorithmName}}</div>
                                    </div>
                                </div> -->
                                <div class="list">
                                    <div class="itemInfo" v-for="(item,ind) in listData" :key="ind">
                                        <div class="left">
                                            <img style="width: 180px" :src="item.webUrl + '/report/stream?id=' + item.id" alt="">
                                        </div>
                                        <div class="right">
                                            <div class="txt"><span class="color">{{ $t('common.alarmTime') }}：</span>{{item.alarmTime}}</div>
                                            <div class="txt"><span class="color">{{$t('components.alarmlistpicture.g71a97')}}</span>{{item.cameraName}}</div>
                                            <div class="txt"><span class="color">{{$t('components.alarmlistpicture.8s4f14')}}</span>{{item.algorithmName}}</div>
                                        </div>
                                    </div>
                                </div>
                            </vue-seamless-scroll>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import vueSeamlessScroll from 'vue-seamless-scroll'
    export default {
        name: 'seamless-scroll',
        components: {
            vueSeamlessScroll
        },
        data() {
            return {
                // 数据
                listData: [],
                imgUrl: 'http://jiangjiang1.natapp1.cc',
            }
        },
        props: {
            dataList: {
                type: Array,
                default: () => {
                    return []
                }
            },
        },
        watch: {
            dataList(val, oVal) {
                this.listData = val;
            }
        },
        computed: {
            optionSetting() {
                return {
                    step: 0.52, // 数值越大速度滚动越快
                    limitMoveNum: this.listData.length, // 开始无缝滚动的数据量 this.dataList.length
                    hoverStop: true, // 是否开启鼠标悬停
                    direction: 1, // 0向下 1向上 2向左 3向右
                    autoPlay: true, // 是否自动滚动
                    openWatch: true, // 开启数据实时监控刷新dom
                    singleHeight: 0, // 单步运动停止的高度(默认值0是无缝不停止的滚动) direction => 0/1
                    singleWidth: 0, // 单步运动停止的宽度(默认值0是无缝不停止的滚动) direction => 2/3
                    waitTime: 1000 // 单步运动停止的时间(默认值1000ms)
                }
            }
        },
        methods: {
            // 处理鼠标点击到哪条，可添加跳转
            handleButton(e) {
                // let InfoAnync = JSON.parse(e.target.dataset.obj)
                // console.log(InfoAnync,' =====> InfoAnync')
                let length = e.path.length
                let labelIndex = -1
                for (let i = 0; i < length; i++) {
                    if (e.path[i].className === 'labelIndex') {
                        labelIndex = i;
                        break
                    }
                }
                if (labelIndex !== -1) {
                    console.log(e.path[labelIndex].innerText, ' =====> e.path[labelIndex].innerText')
                    alert('labelIndex.id = ' + e.path[labelIndex].id + ',title： ' + this.listData[e.path[labelIndex].id].title)
                } else {
                    alert(this.$t('components.alarmlist.4rg3p9'))
                }

            }
        }
    }
</script>

<style lang="scss" scoped>
    .wrap-container {
        background: #08173f;
    }
    .alarmPicture-wrap{
        // width: 340px;
        height: auto;
        margin-bottom: 20px;
        display: flex;
        .picture-style{
            width: 50%;
            height: 100%;
        }
        .alarm-msg{
            // width: calc( 50% - 20px);
            margin-left: 20px;
            height: 170px;
            display: flex;
            flex-direction: column;
            justify-content: space-evenly;
            .msgLine {
                color: #fff;
            }
        }
    }
    .wrap-container{
        background: #08173f;
    }
    .sn-title{
        text-align: center;
    }
    .sn-container{
        // width: 600px;
        height: 340px;
        padding: 20px 30px;
        .seamless-warp{
            height: 310px;
            overflow: hidden;
            visibility: visible;
        }
    }
    .list{
        display: flex;
        flex-wrap: wrap;
        .itemInfo{
            width: 48%;
            padding-right: 10px;
            display: flex;
            align-items: center;
            margin-bottom: 20px;
        }
        .left{
            width: 180px;
        }
        .right{
            padding-left: 10px;
        }
        .txt{
            line-height: 30px
        }
        .color{
            color: #3ed6ff;
        }
    }
    
</style>