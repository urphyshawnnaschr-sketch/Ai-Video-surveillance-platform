<template>
    <div class="app-container-stranger">
        <div class="container-left">
            <retrieveRecords ref="retrieveRecords" @getInfo="getInfo"></retrieveRecords>
        </div>
        <div class="container-right">
            <retrieveResults v-if="currentId" ref="retrieveResults" :currentId="currentId" @refreshFun="refreshFun"></retrieveResults>
            <div v-else style="display: flex;justify-content: center;align-items: center;height: 100%;">
                <img src="@/assets/images/no-data.png" >
            </div>
        </div>
    </div>
</template>
<script>
import retrieveRecords from './components/retrieveRecords.vue';
import retrieveResults from './components/retrieveResults.vue';

export default {
    data() {
        return {
            currentId:'',
        };
    },
    created() {

    },
    methods: {
        getInfo(item){
            this.currentId = item.id;
            let that = this;
            setTimeout(()=>{
                that.$refs.retrieveResults.getData();
            },200)
        },
        refreshFun(){
            this.currentId = ""
            this.$refs.retrieveRecords.getTable()
        }
    },
    components: {
        retrieveRecords,
        retrieveResults,
    },
};
</script>
<style lang="scss">
.app-container-stranger {
    height: calc(100vh - 120px);
    display: flex;
    justify-content: space-between;

    .container-left {
        width: 300px;
        height: 100%;
        background-color: #FFFFFF;
        border-radius: 6px;
        padding: 20px;
        box-sizing: border-box;
        overflow-y: auto;
    }

    .container-right {
        flex: 1;
        height: 100%;
        background-color: #FFFFFF;
        border-radius: 6px;
        padding: 20px;
        box-sizing: border-box;
        overflow-y: auto;
        margin-left: 16px;
    }

    .subheading {
        height: 36px;
        font-size: 16px;
        color: #1C1F23;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        justify-content: space-between;

        .text {
            height: 36px;
            font-weight: bold;
            position: relative;
            display: flex;
            align-items: center;
        }

        .button {
            padding: 5px 16px;
            border-radius: 3px;
            background: #E53935;
            color: #FFFFFF;
            cursor: pointer;
        }

        .text::after {
            content: '';
            display: inline-block;
            width: 100%;
            height: 2px;
            background-color: #EB3A2F;
            position: absolute;
            bottom: 0;
            left: 0;
        }
    }
}
</style>
