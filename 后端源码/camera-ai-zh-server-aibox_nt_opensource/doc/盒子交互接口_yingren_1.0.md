# 接口规范
## 一、概述
本规范旨在定义和描述系统接口的相关要求和标准，确保接口的一致性

<br/>

## 二、术语
主服务器：主服务器负责维护摄像头配置，并提供相关接口给边缘盒子，通过http接口进行互相通信；
边缘盒子：边缘盒子主要用于算法识别服务，将识别结果上报到主服务器；

<br/>
注意： <span style="color: red;"> 字段类型标记为 × 的字段可以根据需要保留或删除</span>

<br/>
<br/>

## 三、主服务器接口定义
### 2.1 边缘盒子注册接口
接口描述：当启动边缘盒子时，将边缘盒子的序列号、厂商，磁盘等信息注册到主服务器
#### 2.2.1 接口地址
接口地址：`/api/aibox/base/register`
#### 2.2.2 请求方法
请求方法：post, application/json
#### 2.2.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| key | string | 是 | 固定值，f5e8df64b91d8915b1805d26c8ffc7c8  |
| sn | string | 是 | 盒子序号  |
| makers | string × | 是 | 制造商  |
| ipAddr | string | 是 | IP地址  |
| deviceMode | string × | 是 | 设备模式  |
| cpuVersion | string × | 是 | CPU型号  |
| kernelVersion | string × | 是 | 内核版本  |
| lyndriverVersion | string × | 是 | 灵犀驱动版本  |
| lynsdkVersion | string × | 是 | 灵犀SDK版本  |
| osVersion | string × | 是 | 操作系统版本  |
| diskTotal | long × | 是 | 磁盘总量  |
| memoryTotal | long × | 是 | 内存总量  |
#### 2.2.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 2.2.5 请求示例
http://192.168.1.115:8021/api/aibox/base/register
```json
{
    "key": "f5e8df64b91d8915b1805d26c8ffc7c8",
    "sn": "L200223C22A00094",
    "makers": "lynxi",
    "ipAddr": "192.168.1.115",
    "deviceMode": "",
    "cpuVersion": "",
    "kernelVersion": "",
    "lyndriverVersion": "",
    "lynsdkVersion": "",
    "osVersion": "",
    "diskTotal": 1234567890,
    "memoryTotal": 123456789,
}
```
#### 2.2.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>

### 2.2 边缘盒子状态信息上报接口 <span style="color: red;"> *(相当于盒子可用状态监控， 应人可以根据需要的字段去处理)*</span>
接口描述：需要定时上报，比如1分钟一次，主要维护边缘盒子心跳时间， 用于判断盒子是否离线
#### 2.2.1 接口地址
接口地址：`/api/aibox/base/status`
#### 2.2.2 请求方法
请求方法：post, application/json
#### 2.2.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| key | string | 是 | 固定值，f5e8df64b91d8915b1805d26c8ffc7c8  |
| sn | string | 是 | 盒子序号  |
| diskUsed | double × | 是 | 磁盘使用量  |
| cpuUsed | double × | 是 | CPU使用率  |
| memoryUsed | double × | 是 | 内存使用量  |
| apuUsed | double × | 是 | APU使用率  |
| vicUsed | double × | 是 | VIC使用率  |
| vicUsed | double × | 是 | VIC使用率  |
| ipeUsed | double × | 是 | IPE帧率  |
| temperatureUsed | double × | 是 | 芯片温度  |
#### 2.2.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 2.2.5 请求示例
http://192.168.1.115:8021/api/aibox/base/status
```json
{
    "key": "f5e8df64b91d8915b1805d26c8ffc7c8",
    "sn": "L200223C22A00094",
    "diskUsed": 50.00,
    "cpuUsed": 50.00,
    "memoryUsed": 50.00,
    "apuUsed": 50.00,
    "vicUsed": 50.00,
    "ipeUsed": 50.00,
    "temperatureUsed": 50.00
}
```
#### 2.2.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>

### 2.3 报警信息上报接口
接口描述：当边缘盒子上的算法识别到报警后，将报警数据上传到主服务器
#### 2.3.1 接口地址
接口地址：`/api/aibox/report`
#### 2.3.2 请求方法
请求方法：post, form-data
#### 2.3.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| key | string | 是 | 固定值，f5e8df64b91d8915b1805d26c8ffc7c8  |
| cameraId | long | 是 | 摄像头id  |
| params | string | 是 | 报警数据，json字符串  |
| file | file | 是 | 图片文件对象  |
| timestamp | string | 否 | 报警时间 yyyy-mm-dd hh:mm:ss  |
#### 2.3.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 2.3.5 请求示例
http://192.168.1.115:8021/api/aibox/report
```json
{
    "key": "f5e8df64b91d8915b1805d26c8ffc7c8",
    "cameraId": 12345,
    "params": '[{"algoId": "123456", "type": "no_wear", "confidence": 0.4, position:[100,100,200,200]}, {"algoId": "123456", "type": "no_wear", "confidence": 0.4, position:[100,100,200,200]}]',
    "file": Object,
    "timestamp": "2024-04-03 11:22:33"
}
```
#### 2.3.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>

### 2.4 人脸报警信息上报接口
接口描述：当边缘盒子上的算法识别人脸数据报警后，将报警数据上传到主服务器
#### 2.4.1 接口地址
接口地址：`/api/face/report`
#### 2.4.2 请求方法
请求方法：post, form-data
#### 2.4.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| key | string | 是 | 固定值，f5e8df64b91d8915b1805d26c8ffc7c8  |
| cameraId | long | 是 | 摄像头id  |
| params | string | 是 | 报警数据，json字符串  |
| file | file | 是 | 图片文件对象  |
| timestamp | string | 否 | 报警时间 yyyy-mm-dd hh:mm:ss  |
#### 2.4.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 2.4.5 请求示例
http://192.168.1.115:8021/api/aibox/report
```json
{
    "key": "f5e8df64b91d8915b1805d26c8ffc7c8",
    "cameraId": 12345,
    "params": '{\"faces\":[{\"bbox\":[377,53,597,273],\"liveness\":\"1\",\"similiarity\":0.017874177545309067,\"user_info\":{\"face_id\":\"1829490311239999490\",\"group_id\":\"1718922509339394048\",\"user_id\":\"1829490311147724802\"}}],\"video_index\":\"1821745970226462722\",\"video_url\":\"rtsp://192.168.0.191:33044/facekorea\"}',
    "file": Object,
    "timestamp": "2024-04-03 11:22:33"
}
```
#### 2.4.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>

### 2.5 摄像头断流通知 <span style="color: red;"> *（新增接口）*</span>
接口描述：摄像头视频流无法连接或者断开后通知
#### 2.5.1 接口地址
接口地址：`/api/aibox/camera/disconnect`
#### 2.5.2 请求方法
请求方法：post, form-data
#### 2.5.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| key | string | 是 | 固定值，f5e8df64b91d8915b1805d26c8ffc7c8  |
| cameraId | long | 是 | 摄像头id  |
#### 2.5.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 2.5.5 请求示例
http://192.168.1.115:8021/api/aibox/camera/disconnect
```json
{
    "key": "f5e8df64b91d8915b1805d26c8ffc7c8",
    "cameraId": 12345
}
```
#### 2.5.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>


## 四、边缘盒子接口定义
### 4.1 边缘盒子摄像头变更接口 <span style="color: red;"> *(部分字段可以不传，或空字符串，但字段类型上×标记的都可以填默认值)*</span>
接口描述：当用户在前端新增、修改、删除、变更绑定算法时，主动调用边缘盒子接口进行通知重启，注意需要固定端口:36896
#### 4.1.1 接口地址
接口地址：`/task/camera`
#### 4.1.2 请求方法
请求方法：post, application/json
#### 4.1.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| cameraId | string | 是 | 摄像头id |
| type | string | 是 | 操作类型，1000 增加摄像头 2000 删除摄像头 3000 启动摄像头  6000 修改摄像头 |
| retry_time | int | 是 | 重试次数 |
| data | object | 是 | 摄像头对象 |
| data.camera_id | string | 摄像头id |
| data.camera_name | string | 摄像头名称 |
| data.rtsp_url | string | 摄像头rtsp地址 |
| data.interval_time | float |  识别间隔(单位：s) |
| data.alarm_interval | float | 告警间隔(单位：s) |
| data.state | int | 默认0， 有效状态,0-有效，其他无效 |
| data.status | int × | 默认写0 摄像头执行状态(1000 推理中 2000 下载算法中 3000 未使用) |
| data.rtsp_push_url | string × | 摄像头推流rtsp地址 |
| data.rtmp_url | string × | 摄像头推流rtmp地址，（废弃）|
| data.action_counter | int × | 动作计数（废弃）， 默认写0 |
| data.algorithms | array | 摄像头关联算法列表 |
| data.algorithms.algorithm_confidence | float | 置信度 |
| data.algorithms.algorithm_version | string × | 关联算法版本 |
| data.algorithms.algorithm_name_en | string | 算法编码 |
| data.algorithms.algorithm_name | string | 算法名称 |
| data.algorithms.algorithm_id | long | 算法id |
| data.algorithms.algorithm_rois | string × | 算法绘制的禁区， json字符串格式，支持多个禁区, 例如： [[{x:1, y:1}, {x:2, y:2}, {x:3, y:3}], [{x: 1, y: 1}, {x:2, y:2}, {x:3, y:3}]] |
| data.algorithms.zip_file | string × | 算法对应的模型文件路径  |
| data.algorithms.md5 | string × | 算法模型对应的md5值  |
#### 4.1.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 4.1.5 请求示例
http://192.168.1.115:36896/task/camera
```json
{
    "cameraId": 0,
    "type": 1000,
    "data": {
        "rtsp_push_url": "rtsp://192.168.1.110:554/livedraw/1774725051870908417",
        "algorithms": [
            {
                "algorithm_confidence": 0.5,
                "algorithm_version": "1.3",
                "algorithm_name_en": "people",
                "algorithm_name": "行人检测",
                "algorithm_id": "1",
                "zip_file": "lynxi/people/lynxi-people-1.3.zip",
                "algorithm_rois": "",
                "md5": "0b512de83933974cf6ec5e77ea243013"
            }
        ],
        "rtsp_url": "rtsp://111.22.69.215:60009/rtp/43021110051180000001_43021110051310000002",
        "alarm_interval": 10.0,
        "action_counter": 1,
        "rtmp_url": "rtmp://192.168.1.110:554/livedraw/1774725051870908417",
        "camera_id": "1774725051870908417",
        "state": 0,
        "camera_name": "工厂施工",
        "interval_time": 5.0,
        "status": 0
    }
}
```
#### 4.1.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```

<br/>
<br/>

### 4.2 边缘盒子重启接口 <span style="color: red;"> *（保留接口，需要的话，可以自行去实现）</span>
接口描述：当用户在前端进行边缘盒子重启操作, 注意需要固定端口:36896
#### 4.2.1 接口地址
接口地址：`/A5F2G1DF20C21D1FxD6FyA1_update`
#### 4.2.2 请求方法
请求方法：post, application/json
#### 4.2.3 请求参数
| 参数名 | 类型 | 是否必传 | 描述 |
| :- | :- | :- | :- |
| hash_value | string | 是 | 固定值：YA5F2G1DF20C21D1FxD6FyA1H  |
#### 4.2.4 响应参数
| 参数名 | 类型 | 描述 |
| :- | :- | :- |
| code | int | 响应状态码, 0-成功 500-失败 |
| msg | string | 响应描述 |
#### 4.2.5 请求示例
http://192.168.1.115:36896/A5F2G1DF20C21D1FxD6FyA1_update
```json
{
    "hash_value": "固定值：YA5F2G1DF20C21D1FxD6FyA1H"
}
```
#### 4.2.6 返回示例
```json
{
    "code": 0,
    "msg": "ok"
}
```
<br/>
<br/>


## 四、算法编码定义
| 算法id | 算法名称 | 算法编码 |
| :- | :- | :- |
 | 	1696809711436365825	 | 	行人检测	 | 	people	 |
 | 	1696809711436365851	 | 	人脸逗留	 | 	faceLinger	 |
 | 	1696809711436365855	 | 	人脸离岗	 | 	faceLeave	 |
 | 	1697225216437583874	 | 	电动车识别	 | 	bicycle	 |
 | 	1697225577508438018	 | 	烟火识别	 | 	fire	 |
 | 	1697226079147196418	 | 	短裤识别	 | 	shorts	 |
 | 	1697236629289762818	 | 	跌倒检测	 | 	fall	 |
 | 	1700413094181273602	 | 	玩手机检测	 | 	phone	 |
 | 	1718802526433247233	 | 	睡岗检测	 | 	dozing	 |
 | 	1723226302428790785	 | 	安全帽	 | 	helmet	 |
 | 	1723607618085240833	 | 	车辆检测	 | 	car	 |
 | 	1734814945089486849	 | 	占道检测	 | 	object	 |
 | 	1734860489178546178	 | 	离岗检测	 | 	absent	 |
 | 	1737308368292884482	 | 	人员超限	 | 	overlimit	 |
 | 	1739161770283687938	 | 	禁区人脸	 | 	peopleface	 |
 | 	1749678005788545025	 | 	反光衣检测	 | 	vest	 |
 | 	1749983170887622657	 | 	垃圾满溢	 | 	trash	 |
 | 	1752273771557494786	 | 	吸烟检测	 | 	smoke	 |
 | 	1759058599374589954	 | 	工作服识别	 | 	uniform	 |
 | 	1759059391364042753	 | 	久坐检测	 | 	sedentariness	 |
 | 	1759060596756680706	 | 	口罩识别	 | 	mask	 |
 | 	1761943104013791234	 | 	疲劳驾驶	 | 	fatigued	 |
 | 	1761943104013791235	 | 	人脸识别	 | 	face_recognize	 |
 | 	1761943104013791236	 | 	手部识别	 | 	handGesture	 |
 | 	1761943104013791237	 | 	梯子站人检测	 | 	ladder	 |
 | 	1761943104013791238	 | 	安全带检测	 | 	harness	 |
 | 	1761943104013791239	 | 	打架识别	 | 	fight	 |
 | 	1761943104013791240	 | 	舰船识别	 | 	boats	 |
 | 	1761943104013791241	 | 	扶梯携带大件行李检测	 | 	luggage	 |
 | 	1761943104013791242	 | 	老鼠检测	 | 	mouse	 |
 | 	1761943104013791243	 | 	猪只检测	 | 	hog	 |
 | 	1761943104013791244	 | 	传送带破损检测	 | 	conveyor	 |
 | 	1761943104013791245	 | 	车辆流向识别	 | 	direction	 |