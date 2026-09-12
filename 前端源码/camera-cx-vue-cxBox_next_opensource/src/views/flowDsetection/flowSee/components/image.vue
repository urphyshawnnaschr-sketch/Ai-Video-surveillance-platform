<template>
  <span> 
    <el-image style="width: 200px;height: 100px;" v-if="url" :preview-src-list="srcList" :src="url"></el-image>
    <span v-else>--</span>
  </span>
</template>
<script>
import { reportImage } from '../api'
import axios from 'axios';
import Cookies from "js-cookie";
export default {
  props:['imgUrl'],
  data(){
    return {
      url:'',
      srcList:[]
    }
  },
  created(){
    this.reportImage()
  },
  methods:{
     reportImage(){
     // const res  = await reportImage({filepath:this.imgUrl})
      let that = this
      axios({
        url: VUE_APP_API_BASE_URL+'/tracker/report/image?filepath='+this.imgUrl,
        method:'get',  //默认方法，可以不写
        responseType:'arraybuffer',  //或者是
        headers:{
          'X-Token':Cookies.get('X-Token')
        }
      }).then(res=>{
        let imageType = res.headers['content-type'];   //获取图片类型
        const blob = new Blob([res.data], { type: imageType })
        const imageUrl = (window.URL || window.webkitURL).createObjectURL(blob)
        that.srcList = [imageUrl]
        that.url = imageUrl
      })

    
      
      
    },
  }
}
</script>