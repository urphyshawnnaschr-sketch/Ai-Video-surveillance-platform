<template>
    <el-drawer
    :visible.sync="drawer"
    :modal="false"
    show-close
    size="700px"
    :wrapperClosable="false"
    @close="close"
    >
        <div slot="title" style="font-size: 16px;font-weight: bold;">{{$t('components.facedetail.2i3s3m')}}</div>
        <div class="detail-cont" v-loading="loading">
            <div class="title">{{$t('components.facedetail.31o3fr')}}</div>
            <div class="flex-item">
                <div>
                    <div style="text-align: center;" v-if="params.id">
                        <el-image
                            style="width: 200px; height: 200px;margin-bottom: 16px;"
                            :preview-src-list="[getFaceReportImageUrl(params.id)]"
                            :src="getFaceReportImageUrl(params.id)"
                        >
                        </el-image>
                    </div>
                    <div class="flex">
                        <div class="tip-title">{{$t('groupview.index.30617c')}}</div>
                        <div>{{ params.cameraName }}</div>
                    </div>
                    <div class="flex">
                        <div class="tip-title">{{$t('faceControl.faceRecognition.snapTime')}}</div>
                        <div>{{ params.reportTime }}</div>
                    </div>
                </div>
                <div style="text-align: center;">
                    <div class="number">{{params.similiarity?params.similiarity+'%':0}}</div>
                    <div class="tip">{{$t('components.facedetail.tp15f4')}}</div>
                    <div class="red-text">{{ params.stranger }}</div>
                </div>
                <div>
                    <div style="text-align: center;">
                        <el-image
                            style="width: 200px; height: 200px;margin-bottom: 16px;"
                            :preview-src-list="[getFaceAvatarImageUrl(params.userId)]"
                            :src="getFaceAvatarImageUrl(params.userId)"
                        >
                        </el-image>
                        
                    </div>
                    <div class="flex">
                        <div class="tip-title">{{$t('components.facedetail.5g81u6')}}</div>
                        <div>{{ params.userName }}</div>
                    </div>
                    <div class="flex">
                        <div class="tip-title">{{$t('faceControl.faceRecognition.belongingGroup')}}</div>
                        <div>{{ params.groupName }}</div>
                    </div>
                </div>
            </div>
            <div class="title">{{$t('components.facedetail.h7x3hl')}}</div>
            <div v-if="params.id" style="margin-top: 16px;">
                <el-image
                            style="width: 100%;"
                            :preview-src-list="[
                            VUE_APP_API_BASE_URL +
                                '/face/report/image?id=' +
                                params.id + '&type=1',
                            ]"
                            :src="
                            VUE_APP_API_BASE_URL +
                            '/face/report/image?id=' +
                            params.id + '&type=1'
                            "
                        >
                        </el-image>
            </div>
            <div class="bottom">
            <div class="button-box">
                <el-button-group>
                    <el-button type="primary" icon="el-icon-arrow-left" @click="searchNearlyFun(0)">{{$t('components.facedetail.6225p8')}}</el-button>
                    <el-button type="primary" @click="searchNearlyFun(1)">{{$t('components.facedetail.3udxki')}}<i class="el-icon-arrow-right el-icon--right"></i></el-button>
                </el-button-group>
                <div>
                    <el-button @click="downloadFun(0)">{{$t('components.facedetail.8ado4p')}}</el-button>
                    <el-button @click="downloadFun(1)">{{$t('components.facedetail.ih0cwo')}}</el-button>
                </div>
            </div>
        </div>
        </div>
    </el-drawer>
</template>

<script>
import Cookies from "js-cookie";
import { searchNearly } from "../api"
import { faceInfo,dowloadImage, } from "../../faceHistory/api"
export default {
    props:{
        currentId:{
            type:String,
            default:""
        },
        currentSearchId:{
            type:String,
            default:""
        }
    },
    data() {
        return {
            drawer:true,
            params:{},
            detailId:"",
            VUE_APP_API_BASE_URL,
            loading:false
        };
    },
    created(){
        this.detailId = this.currentId;
        this.getDetail();
    },
    methods:{
        close(){
            this.$emit('close')
        },
        // 获取详情
        async getDetail(){
            this.loading = true;
            const { data } = await faceInfo({id:this.detailId});
            this.params = data;
            this.params.similiarity = parseFloat(data.similiarity * 100).toFixed(2)
            this.loading = false
        },
        // 获取上一条、下一条
        searchNearlyFun(type){
            let obj = {
                reportId:this.detailId,
                type:type,
                searchId:this.currentSearchId
            }
            searchNearly(obj).then(res=>{
                if(res.data !='0' && res.data){
                    this.detailId = res.data;
                    this.getDetail()
                }else{
                    this.$message.warning($t('common.noData'))
                }
            })
        },
        // 下载图片
        downloadFun(type) {
            dowloadImage({id:this.detailId,type:type}).then(res=>{
                var blob = new Blob([res.data], { type: "image/png" });
                var url = window.URL.createObjectURL(blob);
                var linkElement = document.createElement("a");
                linkElement.setAttribute("href", url);
                linkElement.setAttribute("downLoad", type==1?"cutDiagram":'originalDrawing');
                linkElement.click();
                document.body.removeChild(linkElement);
            })
        },
        // 获取人脸头像图片
        getFaceAvatarImageUrl(id) {
            const token = Cookies.get('X-Token');
            return `${VUE_APP_API_BASE_URL}/face/image/avatar?userId=${id}&X-Token=${token}&_t=${new Date().getTime()}`;
        },
        // 获取人脸报警原图图片
        getFaceReportSourceImageUrl(id) {
            const token = Cookies.get('X-Token');
            return `${VUE_APP_API_BASE_URL}/face/report/image?id=${id}&type=1&X-Token=${token}&_t=${new Date().getTime()}`;
        },
        // 获取人脸报警图片
        getFaceReportImageUrl(id) {
            const token = Cookies.get('X-Token');
            return `${VUE_APP_API_BASE_URL}/face/report/image?id=${id}&X-Token=${token}&_t=${new Date().getTime()}`;
        },
    }
};
</script>
<style scoped lang="scss">
.detail-cont{
    padding: 16px;
    position: relative;
    .title{
        font-size: 16px;
        line-height: 24px;
        color: #303133;
    }
    .flex-item{
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 16px;
        margin-bottom: 32px;
    }
    .flex{
        display: flex;
        align-items: center;
        font-size: 14px;
        color: #303133;
        margin-bottom: 5px;
        line-height: 22px;
    }
    .tip-title{
        width: 70px;
        text-align: right;
        color: #909399;
        margin-right: 10px;
        
    }
    .number{
        font-size: 32px;
        color: #303133;
    }
    .tip{
        padding: 16px 0px;
        font-size: 16px;
        color: #909399;
        line-height: 22px;
    }
    .red-text{
        color: #FF0000;
        font-size: 24px;
        line-height: 22px;
    }
}
.bottom{
    // position: fixed;
    // bottom: 0px;
    // padding: 16px;
    margin-top: 16px;
    .button-box {
        display: flex;
        align-items: center;
        justify-content: space-between;
    }
}
</style>
