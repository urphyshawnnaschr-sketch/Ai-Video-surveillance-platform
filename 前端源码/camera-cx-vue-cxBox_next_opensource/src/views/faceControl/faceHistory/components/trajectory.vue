<template>
    <el-drawer
    :visible.sync="drawer"
    :modal="false"
    show-close
    size="70%"
    :wrapperClosable="false"
    direction="btt"
    @close="close"
    >
        <div slot="title" class="flex-item">
            <div class="flex-item">
                <div class="user title">{{ cameraObj.userName }}</div>
                <div class="title">{{ cameraObj.groupName }}</div>
            </div>
            <div class="flex-item" style="margin-left: 100px;">
                <div class="mr">
                    <div class="tip-title">{{$t('components.trajectory.8i64t4')}}</div>
                    <div class="tip-cont">{{ cameraObj.reportCount }}{{$t('components.trajectory.715618')}}</div>
                </div>
                <div class="mr">
                    <div class="tip-title">{{$t('components.trajectory.l1jo3i')}}</div>
                    <div class="tip-cont">{{ cameraObj.endCameraName }}</div>
                </div>
                <div class="mr">
                    <div class="tip-title">{{$t('components.trajectory.c35kz3')}}</div>
                    <div class="tip-cont">{{ cameraObj.endCameraDate }}</div>
                </div>
                <div class="mr">
                    <div class="tip-title">{{$t('components.trajectory.f37428')}}</div>
                    <div class="tip-cont">{{ cameraObj.startCameraName }}</div>
                </div>
                <div>
                    <div class="tip-title">{{$t('components.trajectory.0b2g66')}}</div>
                    <div class="tip-cont">{{ cameraObj.startCameraDate }}</div>
                </div>
            </div>
        </div>
        <div style="display: flex; justify-content: space-between;padding: 0px 16px;">
            <el-form label-position="right" label-width="60px" style="display: flex;flex-wrap: wrap;">
            
                <el-form-item :label="$t('groupview.index.30617c')">
                    <el-select
                        v-model="params.cameraIds"
                        multiple
                        collapse-tags
                        filterable 
                        style="width: 150px"
                        :placeholder="$t('common.chooseText')">
                        <el-option
                            v-for="item in cameraList"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
            
                <el-form-item :label="$t('faceControl.faceRecognition.date')">
                    <el-date-picker
                        v-model="params.date"
                        style="width: 330px;"
                        :default-time="['00:00:00', '23:59:59']"
                        type="datetimerange"
                        :range-separator="$t('common.pickerDate.to')"
                        format="yyyy-MM-dd HH:mm:ss"
                        value-format="yyyy-MM-dd HH:mm:ss"
                        :start-placeholder="$t('common.pickerDate.startDate')"
                        :end-placeholder="$t('common.pickerDate.endDate')">
                    </el-date-picker>
                </el-form-item>
            </el-form>
            <div>
                <el-button type="primary" icon="el-icon-search" @click="getTable">{{$t('button.queryText')}}</el-button>
                <el-button icon="el-icon-refresh" @click="reset">{{$t('button.resetText',{text:''})}}</el-button>
            </div>
        </div>
        <div>
            <div class="flexbg" ref="flexbg" v-if="dataSource.length">
                <div class="flexItem" v-for="item in dataSource">
                <div>
                    <el-image
                            style="width: 240px; height: 200px"
                            :preview-src-list="[getFaceReportImageUrl(item.id)]"
                            :src="getFaceReportImageUrl(item.id)"
                        >
                            <div slot="error" class="image-slot">
                            <i class="el-icon-picture-outline"></i>
                            </div>
                        </el-image>
                </div>
                <div class="text" style="width: 100%;">
                    <div style="padding: 10px;">
                        <div class="flex">
                            <div class="text-title">{{$t('groupview.index.30617c')}}</div>
                            <div class="text-cont" >{{ item.cameraName }}</div>
                        </div>
                        <div  class="flex">
                            <div class="text-title">{{$t('faceControl.faceRecognition.snapTime')}}</div>
                            <div class="text-cont">{{ item.reportTime }}</div>
                        </div>
                        <div v-if="item.strangerType!=1"  class="flex">
                            <div class="text-title">{{$t('faceControl.faceRecognition.belongingGroup')}}</div>
                            <div class="text-cont">{{ item.groupName }}</div>
                        </div>
                        </div>
                    </div>
                </div>
            </div>
            <div v-else>
                <el-empty :description="$t('common.noData')"></el-empty>
            </div>
            <div class="pagination" v-if="dataSource.length">
                <el-pagination
                background
                :current-page="params.page"
                :page-size="params.limit"
                layout="total, sizes, prev, pager, next, jumper"
                :total="params.total"
                @current-change="handleCurrentChange"
                @size-change="handleSizeChange"
                ></el-pagination>
            </div>
        </div>
    </el-drawer>
</template>

<script>
import Cookies from "js-cookie";
import { listUserInfo,listUserPage,listData2 } from "../api"
export default {
    props:{
        currentItme:{
            type:Object,
            default:{}
        }
    },
    data() {
        return {
            VUE_APP_API_BASE_URL,
            drawer:true,
            params:{
                cameraIds:[],
                date:[],
                page:1,
                limit:10,
                total:0,
            },
            cameraObj:{},
            cameraList:[],
            dataSource:[]
        };
    },
    created(){
        this.getCamera();
        this.getInfo();
        this.getTable();
    },
    methods:{
        // 获取数据
        async getTable(){
            let obj = { 
                userId:this.currentItme.userId,
                cameraIds:this.params.cameraIds,
                limit: this.params.limit,
                page: this.params.page,
            }
            if(this.params.date && this.params.date.length>0){
                obj.startDate=this.params.date[0];
                obj.endDate=this.params.date[1];
            }
            this.loading = true
            const { data,count } = await listUserPage(obj)
            this.dataSource = data
            this.params.total = parseInt(count)
            this.loading = false
        },
        handleCurrentChange(val) {
            this.params.page = val;
            this.getTable();
        },
        handleSizeChange(val) {
            this.params.limit = val;
            this.params.page = 1;
            this.getTable();
        },
        reset(){
            this.params = {
                cameraIds:[],
                date:[],
                page:1,
                limit:10,
                total:0,
            }
            this.getTable()
        },
        async getCamera(){
            //const res = await listData2();
            //this.cameraList = res.data;
        },
        async getInfo(){
            let obj = {
                userId:this.currentItme.userId,
                cameraIds:this.params.cameraIds,
            }
            if(this.params.date && this.params.date.length>0){
                obj.startDate=this.params.date[0];
                obj.endDate=this.params.date[1];
            }
            const res = await listUserInfo(obj);
            this.cameraObj = res.data;
            this.cameraList = res.data.cameras;
        },
        close(){
            this.$emit('close')
        },
        // 获取人脸报警图片
        getFaceReportImageUrl(id) {
            const token = Cookies.get('X-Token');
            return `${VUE_APP_API_BASE_URL}/face/report/image?id=${id}&X-Token=${token}`;
        },
    }
};
</script>
<style scoped lang="scss">
.flex-item{
    display: flex;
    align-items: center;
}
.user{
    font-weight: bold;
    border-bottom: 1px solid #EB3A2F;
    margin-right: 20px;
}
.title{
    font-size: 16px;
    line-height: 24px;
    color: #000;
}
.tip-title{
    font-size: 14px;
    color: #606266;
    margin-bottom: 10px;
}
.tip-cont{
    font-size: 20px;
    color: #303133;
    height: 30px;
}
.mr{
    margin-right: 24px;
}
.flexbg{
    width: 100%;
    padding: 0px 24px;
    display: grid;
    justify-content: space-between;
    grid-template-columns: repeat(auto-fill, 240px);
    grid-gap: 15px;
}
.tits{
    width: 60px;
    height: 20px;
    overflow: hidden;
    white-space: nowrap;
    text-overflow: ellipsis;
    color: #303133;
    font-size: 16px;
}
.flexItem{
    display: inline-flex;
    margin-bottom: 8px;
    flex-direction: column;
    align-items: flex-start;
    gap:  8px;
    border-radius: 6px;
    border: 1px solid #E4E7ED;
    background: #FFF;
}
.flexItem:hover{
    box-shadow: 0px 12px 32px 0px rgba(0, 0, 0, 0.04), 0px 8px 20px 0px rgba(0, 0, 0, 0.08);
}
.text-flex{
    padding: 10px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.text-title{
    font-size: 12px;
    color: #909399;
    margin-right: 10px;
    text-align: right;
    width: 50px;
    line-height: 20px;
}
.text-cont{
    color: #060606;
    line-height: 20px;
}
.flex{
    display: flex;
}
</style>
