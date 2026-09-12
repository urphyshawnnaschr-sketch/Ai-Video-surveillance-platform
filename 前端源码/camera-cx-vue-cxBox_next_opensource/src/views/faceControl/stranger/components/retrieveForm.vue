<template>
    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="600px" :close-on-click-modal="false">
        <el-form ref="formData" :model="formData" :rules="rules" label-width="125px" :disabled="formDisabled">
            <el-form-item :label="$t('components.retrieveform.5mw7vm')" prop="filename">
                <!-- <el-upload class="avatar-uploader" :disabled="formDisabled" action="" :show-file-list="false"
                    :on-success="handleVotingSuccess">
                    <img v-if="formData.imgUrlEcho" :src="formData.imgUrlEcho" alt="Image" class="avatar">
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    <div class="el-upload__tip" slot="tip">Uploadjpg/pngFile，Not Exceed1M，and must only contain target face。</div>
                </el-upload> -->

                <el-upload
                    class="avatar-uploader" :disabled="formDisabled"
                    action=""
                    :http-request="handleUploadImg"
                    :show-file-list="false"
                    accept=".jpg,.jpeg,.png,.gif"
                    style="display: inline"
                    >
                    <div v-if="formData.filename" class="file-img">
                        <img
                        :src="getFaceTrackSearchImageUrl(formData.filename)"
                        class="avatar"
                        />
                    </div>
                    <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    <div class="el-upload__tip" slot="tip">{{$t('components.retrieveform.7k6v8g')}}</div>
                </el-upload>
            </el-form-item>
            <el-form-item :label="$t('mapimagemanagent.index.h8zi52')" prop="groupIds">
                <el-cascader
                v-model="formData.groupIds"
                :options="groupList"
                :props="props"
                collapse-tags
                style="width: 100%;"
                clearable></el-cascader>
            </el-form-item>
            <el-form-item :label="$t('flowDsetection.flowSee.dateTime')" prop="date">
                <el-date-picker v-model="formData.date" value-format="yyyy-MM-dd HH:mm:ss" type="datetimerange"
                    :default-time="['00:00:00', '23:59:59']" :range-separator="$t('common.pickerDate.to')"
                    :start-placeholder="$t('addcamera.timeinfo.5r5j4x')" :end-placeholder="$t('addcamera.timeinfo.dc2916')" style="width: 100%;">
                </el-date-picker>
            </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false" size="mini">{{$t('button.cancelText',{text:' '})}}</el-button>
            <el-button type="primary" size="mini" @click="submitForm" :disabled="issave">{{ issave?$t('components.retrieveform.669zw0'):$t('components.retrieveform.rx222n') }}</el-button>
        </div>
    </el-dialog>
</template>

<script>
import Cookies from "js-cookie";
import { upload,groupTree,searchSave } from '../api'
export default {
    name: "activity",
    data() {
        return {
            issave:false,
            groupList:[],
            props: { 
                multiple: true,
                value:'id',
                label:'name',
                children:'children'
            },
            // 标题
            dialogTitle: $t('components.retrieveform.1n118p'),
            // 是否显示
            dialogVisible: false,
            // 表单参数
            formData: {
                filename:'',
                groupIds:[],
                date:[],
            },
            // 表单禁用状态
            formDisabled: false,
            // 表单校验
            rules: {
                filename: [{ required: true, message: $t('components.retrieveform.p1w5l2'), trigger: "change" }],
                groupIds: [{ required: true, message: $t('components.retrieveform.6fk843'), trigger: "change" }],
                date: [{ required: true, message: $t('components.retrieveform.162266'), trigger: "change" }],
            },
            VUE_APP_API_BASE_URL
        }
    },
    created() {
        this.getGroup();
    },
    methods: {
        // 获取分组
        async getGroup(){
            const res = await groupTree();
            if (res.data && res.data.length > 0) {
                this.groupList = this.getTreeData(res.data);
            }
        },
        getTreeData(data) {
        data.forEach((item) => {
            if (item.children.length < 1) {
            item.children = undefined;
            } else {
            this.getTreeData(item.children);
            }
        });
        return data;
        },
        // 打开
        openDialog(id, type) {
            this.formDisabled = false
            this.formData = {
                filename:'',
                groupIds:[],
                date:[],
            };
            this.$refs['formData']?.resetFields();
            this.dialogVisible = true;
        },
        // 上传
        async handleUploadImg(files) {
            const file = files.file ? files.file : files[0];
            // 渲染上传文件
            let formData = new FormData();
            formData.append("file", file);
            const data = await upload(formData);
            this.formData.filename = data.data;
        },
       
        // 提交表单
        submitForm() {
            this.$refs["formData"].validate(valid => {
                if (valid) {
                    this.issave = true;
                    let arr = this.formData.groupIds;
                    let groupArr = []
                    if(arr.length>0){
                        const flattenedArray = [].concat(...arr);
                        groupArr = [...new Set(flattenedArray)]
                    }     
                    // 参数处理
                    let params = {
                        groupIds:groupArr,
                        startDate:this.formData.date[0],
                        endDate:this.formData.date[1],
                        filename:this.formData.filename
                    }
                    searchSave(params).then(res=>{
                        this.$message.success(this.$t('components.retrieveform.4j2dgn'));
                        this.dialogVisible = false;
                        this.$parent.getTable();
                        this.issave = false;
                    })
                }
            })
        },
        // 获取检索历史图片
        getFaceTrackSearchImageUrl(filename) {
            const token = Cookies.get('X-Token');
            return `${VUE_APP_API_BASE_URL}/face/track/search/image?filename=${filename}&X-Token=${token}`;
        }
    },
}
</script>

<style scoped lang="scss">
::v-deep .el-form--inline .el-form-item__content,
.el-input-number--medium {
    width: 175px;
    margin-left: 0 !important;
}

::v-deep .avatar-uploader .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
}

::v-deep .avatar-uploader .el-upload:hover {
    border-color: #E53935;
}

::v-deep .avatar-uploader-icon {
    font-size: 28px;
    color: #8c939d;
    width: 128px;
    height: 128px;
    line-height: 128px;
    text-align: center;
}

::v-deep .avatar {
    width: 128px;
    height: 128px;
    display: block;
}
</style>
