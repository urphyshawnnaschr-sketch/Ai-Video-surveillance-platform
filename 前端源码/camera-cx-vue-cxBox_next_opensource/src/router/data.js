const allRoutess = [
    {
      path: '/',
      name:"index",
      component: () => import('@/layout/index'),
      redirect: '',//'/algorithmManagement/modelTesting',
      children:[
        // {
        //   icon: 'el-icon-s-grid',
        //   name: '地图管理',
        //   path: '/systemManage/mapImageManagent',
        //   component: () => import('@/views/applicationMonitoring/systemManage/mapImageManagent/index.vue'),
        // },
        // {
        //   component: () => import('@/views/applicationMonitoring/modelTesting/modelDetail'),
        //   name: '算法详情',
        //   path: '/algorithmManagement/modelDetail',
        //   meta: {
        //     hideMenu: true
        //   }
        // },
      //   {
      //     icon: 'el-icon-s-cooperation',
      //     name: '边缘平台',
      //     path: '/edgePlatform',
      //     component: () => import('@/views/applicationMonitoring/index'),
      //     children:[
      //       {
      //         name: '点位管理',
      //         path: '/edgePlatform/boxManagement',
      //         component: () => import('@/views/applicationMonitoring/boxManagement'),
      //       },
      //       {
      //         name: '盒子管理',
      //         path: '/edgePlatform/casketManagement',
      //         component: () => import('@/views/applicationMonitoring/casketManagement/index'),
      //       },
      //       // {
      //       //   name: '旧视频预览',
      //       //   path: '/edgePlatform/videoPreview',
      //       //   component: () => import('@/views/applicationMonitoring/edgePlatform/videoPreview'),
      //       // },
      //       {
      //         name: '视频预览',
      //         path: '/edgePlatform/newVideoPreview',
      //         component: () => import('@/views/applicationMonitoring/edgePlatform/newVideoPreview'),
      //       },
      //       {
      //         name: '程序升级',
      //         path: '/edgePlatform/updata',
      //         component: () => import('@/views/applicationMonitoring/casketManagement/edgePlatformUpdata'),
      //       },{
      //         name: '日志查询',
      //         path: '/systemManagement/logInfo',
      //         component: () => import('@/views/applicationMonitoring/logInfo'),
      //       }
      //     ]
      //   },
      //   {
      //     icon: 'el-icon-s-data',
      //     name: '告警管理',
      //     path: '/dataManagement/alarmManagement',
      //     component: () => import('@/views/applicationMonitoring/alarmManagement'),
      //   },
      //   {
      //     icon: 'el-icon-microphone',
      //     name: '音柱管理',
      //     path: '/dataManagement/soundColumnManagement',
      //     component: () => import('@/views/applicationMonitoring/soundColumnManagement'),
      //   },
      //   {
      //     icon: 'el-icon-s-comment',
      //     name: '推送管理',
      //     path: '/noticeManagement',
      //     component: () => import('@/views/applicationMonitoring/index'),
      //     children:[
      //       {
      //         name: '短信推送',
      //         path: '/noticeManagement/messageManagement',
      //         component: () => import('@/views/applicationMonitoring/noticeManagement/messageManagement'),
      //       },
      //       {
      //         name: '办公推送',
      //         path: '/noticeManagement/wxManagement',
      //         component: () => import('@/views/applicationMonitoring/noticeManagement/wxManagement'),
      //       },
      //       {
      //         name: '接囗推送',
      //         path: '/noticeManagement/apiManagement',
      //         component: () => import('@/views/applicationMonitoring/noticeManagement/apiManagement'),
      //       },
      //       {
      //         name: '语音推送',
      //         path: '/noticeManagement/voiceManagement',
      //         component: () => import('@/views/applicationMonitoring/noticeManagement/voiceManagement'),
      //       },
      //       {
      //         name: 'IP音柱播报',
      //         path: '/noticeManagement/IPSoundColumnManagement',
      //         component: () => import('@/views/applicationMonitoring/noticeManagement/IPSoundColumnManagement'),
      //       },
      //     ]
      //   },
      //   {
      //     icon: 'el-icon-s-tools',
      //     name: '系统管理',
      //     path: '/systemManagement',
      //     component: () => import('@/views/applicationMonitoring/index'),
      //     children:[
      //       {
      //         name: '基础配置',
      //         path: '/systemManagement/baseManagement',
      //         component: () => import('@/views/applicationMonitoring/systemManagement'),
      //       },
      //       {
      //         name: '组织账号管理',
      //         path: '/systemManagement/organizationalManagement',
      //         component: () => import('@/views/applicationMonitoring/systemManage/organizationalManagement/index'),
      //       },
      //       {
      //         name: '角色管理',
      //         path: '/systemManagement/roleManagement',
      //         component: () => import('@/views/applicationMonitoring/systemManage/roleManagement/index'),
      //       },
      //       {
      //         name: '菜单管理',
      //         path: '/systemManagement/menuManagement',
      //         component: () => import('@/views/applicationMonitoring/systemManage/menuManagement/index'),
      //       },
      //       // {
      //       //   name: '账号管理',
      //       //   path: '/systemManagement/accountManagement',
      //       //   component: () => import('@/views/applicationMonitoring/accountManagement'),
      //       // }
      //     ]
      //   },
      //   {
      //   icon: 'el-icon-s-cooperation',
      //   name: '人脸布控',
      //   path: '/faceControl',
      //   component: () => import('@/views/faceControl/index'),
      //   children: [
          
      //     {
      //       component: () => import('@/views/faceControl/faceRecognition'),
      //       name: '实时识别',
      //       path: '/faceControl/faceRecognition'
      //     },
      //     {
      //       component: () => import('@/views/faceControl/faceManagent/index'),
      //       name: '人脸管理',
      //       path: '/faceControl/faceManagent'
      //     },
      //     {
      //       component: () => import('@/views/faceControl/faceHistory/index'),
      //       name: '识别记录',
      //       path: '/faceControl/faceHistory'
      //     },
      //     {
      //       component: () => import('@/views/faceControl/faceCompare/index'),
      //       name: '人脸比对',
      //       path: '/faceControl/faceCompare'
      //     }
      //   ]
      // },
      ]
    },
    
    {
      name: 'Alarm Management-1',
      path: '/dataManagement/alarmManagement1',
      component: () => import('@/views/applicationMonitoring/alarmManagementCopy'),
      hidden: true
    },
    {
      path: '/login',
      component: () => import('@/views/login'),
      hidden: true
    },
    {
      path: '/bigScreen',
      component: () => import('@/bigScreen/index.vue'),
      hidden: true
    },
]

export default allRoutess;
