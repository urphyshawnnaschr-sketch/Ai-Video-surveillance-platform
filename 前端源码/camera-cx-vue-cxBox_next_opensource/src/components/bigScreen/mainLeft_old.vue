<template>
    <div class="main1">
        <div class="mainLeft"> 
            <Title :name="$t('bigscreen.mainleft.kp3c04')" />
            <div class="creamBox">
                <div class="numbg1">
                    <div style="height: 30px;font-size: 40px;margin-bottom: 10px">
                        {{runingNum}}
                        <span style="font-size: 16px">{{$t('bigscreen.mainleft.8ye8wj')}}</span>
                    </div>
                    <div style="height: 30px;font-size: 16px">{{$t('bigscreen.mainleft.p122g2')}}</div>
                </div>
                <div class="numbg2">
                    <div style="height: 30px;font-size: 40px;margin-bottom: 10px">
                        {{totleNum}}
                        <span style="font-size: 16px">{{$t('bigscreen.mainleft.8ye8wj')}}</span>
                    </div>
                    <div style="height: 30px;font-size: 16px">{{$t('bigscreen.mainleft.96uz76')}}</div>
                </div>
            </div>
            <Title :name="$t('bigscreen.mainleft.tdget5')" />
            <div style="width: 410px;height: 326px;">
                <Category :show="true"/>
            </div>
            <Title :name="$t('bigscreen.mainleft.2dvar7')" />
            <div>
                <alarmList :data="warningList"/>
            </div>
        </div>
        <div class="mainCenter">
            <Title :name="$t('bigscreen.mainleft_old.lkv7t0')" :isShow="true" />
            <div class="sysBox">
                <sysMage v-for="(item,index) in systemData" :key="index" :name="item.name" :url="item.src" :num="item.num"></sysMage>
            </div>
            <Title :name="$t('bigscreen.mainleft_old.ojyuw7')" :isShow="true" />
            <div style="display: flex">
                <div style="width: 50%;height: 340px;">
                    <annular :dataArr="countAlgorithm1DayArr" :show="true"/>
                </div>
                <div>
                    <Map :show="true" />
                </div>
            </div>
            <Title :name="$t('bigscreen.mainleft.wxj36q')" :isShow="true" />
            <div>
                <alarmListPicture :dataList="warningList"/>
            </div>
        </div>
    </div>
</template>

<script>
import Title from './Title';
import request from '@/utils/request.js'
import Category from '@/components/echart/earlyWarning'
import alarmList from "@/components/alarmListCopy";
import sysMage from '@/components/systemManageCopy'
import annular from '@/components/echart/annular'
import Map from '@/components/echart/map'
import alarmListPicture from "@/components/alarmListPictureCopy";
import ScoketService from "@/utils/websocket.js";
export default {
    data() {
        return {
            runingNum: 0,
            totleNum: 0,
            warningList: [],

            systemData: [
                {
                    src: require('@/assets/img/resBg.png'),
                    name: this.$t('bigscreen.mainleft.2iv356'),
                    label: 'server_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg1.png'),
                    name: this.$t('bigscreen.mainleft.5sq66y'),
                    label: 'dataset_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg2.png'),
                    name: this.$t('bigscreen.mainleft.756bc9'),
                    label: 'today_dataset_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg3.png'),
                    name: this.$t('bigscreen.mainleft.wbbe1p'),
                    label: 'mark_dataset_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg4.png'),
                    name: this.$t('bigscreen.mainleft.28lg5d'),
                    label: 'task_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg5.png'),
                    name: this.$t('bigscreen.mainleft.948w0b'),
                    label: 'model_train_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg6.png'),
                    name: this.$t('bigscreen.mainleft.p7q864'),
                    label: 'model_deploy_count',
                    num: 0
                },
                {
                    src: require('@/assets/img/resBg7.png'),
                    name: this.$t('bigscreen.mainleft.k89i55'),
                    label: 'model_image_count',
                    num: 0
                }
            ],

            countAlgorithm1DayArr: [],
            ws: null,
        }
    },
    components: {
        Title,
        Category,
        alarmList,
        sysMage,
        annular,
        Map,
        alarmListPicture
    },
    mounted() {
        this.getCountCamera();
        this.getCountNewly();
        this.timer = setInterval(() => {

            this.getCountNewly(); // 发送接口请求

        }, 10000); // 每10秒请求一次接口
        this.getCountData();
        this.getCountAlgorithm1Day();
        // ScoketService.Instance.connect(
        //     `${VUE_APP_WS_BASE_URL}/report/${this.guid()}`
        // );
        // ScoketService.Instance.send({
        //     action: "REPORT_SHOW",
        // });
        // ScoketService.Instance.registerCallBack("REPORT_SHOW", (data) => {
        //     this.warningList.push(data)
        // });
    },
    beforeDestroy() {
        // 在组件销毁前清除定时器
        clearInterval(this.timer);
        // 关闭
        // ScoketService.Instance.onClose()
    },
    methods: {
        getCountCamera() {
            request.get('/statistic/countCamera').then(({data}) => {
                this.runingNum = data.runing;
                this.totleNum = data.total;
            })
        },
        getCountNewly() {
            request.get('/statistic/countNewly').then(({data}) => {
                this.warningList = data;
            })
        },
        guid() {
            return 'xxx-xxx-4y'.replace(/[xy]/g, function (c) {
                var r = Math.random() * 16 | 0,
                    v = c == 'x' ? r : (r & 0x3 | 0x8);
                return v.toString(16);
            });
        },
        getCountData() {
            request.get('/statistic/countData').then(({data}) => {
                for(let i in data) {
                    for(let item of this.systemData) {
                        if(item.label == i) {
                            item.num = data[i]
                        }
                    }
                }
            })
        },
        getCountAlgorithm1Day() {
            request.get('/statistic/countAlgorithm1Day').then(({data}) => {
                this.dayNum = data.total;
                let resData = data.datas.length > 7 ? data.datas.slice(0,7) : data.datas;
                for(let item of resData) {
                    let obj = {
                        name: item.name,
                        value: item.count
                    }
                    this.countAlgorithm1DayArr.push(obj);
                }
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.main1 {
    display: flex;
}
.mainLeft {
    width: 510px;
    .creamBox {
        display: flex;
        justify-content: space-around;
        .numbg1, .numbg2 {
            background: url('@/assets/images/yuan1.png') no-repeat;
            width: 142px;
            height: 142px;
            text-align: center;
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: center;
            color: #fff;
        }
        .numbg2 {
            background: url('@/assets/images/yuan2.png') no-repeat;
        }
    }
}

.mainCenter {
    width: 810px;
    .sysBox {
        display: flex;
    }
}
</style>