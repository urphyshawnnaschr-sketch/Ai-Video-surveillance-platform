export default class ScoketService {
    instance = null;  //页面中使用的实例
    ws = null; // 和服务端连接的对象
    connected = false;
    sendRetryCounnt = 0;
    connectRetryCount = 0;
    manualClose = false;
    callBackMapping = {};

    pingTimeoutId = null;
    pongTimeoutId = null;
    pingMsg = {
        action: "REPORT_SHOW",
    };
    pingTimeout = 15000;
    pongTimeout = 10000;
    static get Instance() {
        if (!this.instance) {
            this.instance = new ScoketService();
        }
        return this.instance;
    }
    guid() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0,
                v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }
    connect(url) {
        if (!window.WebSocket) {
          return console.log("Your browser does not support");
        }
        this.ws = new WebSocket(url);
        // 监视打开事件
        this.ws.onopen = () => {
          this.connected = true;
          this.connectRetryCount = 0;
          // 心跳监测重置
          this.heartCheck();
        };
        // 监视关闭事件
        this.ws.onclose = () => {
          this.connected = false;
          this.connectRetryCount++;
    
          if (this.manualClose) {
            console.log("Manually closed");
            return;
          }
          setTimeout(() => {
            this.connect(url);
          }, 500 * this.connectRetryCount);
        };
        // 监听服务器传递数据
        this.ws.onmessage = (msg) => {
          this.heartCheck();
          const recvData = JSON.parse(msg.data);
          const socketType = recvData.type;
          if (this.callBackMapping[socketType]) {
            if (recvData) {
              this.callBackMapping[socketType].call(this, recvData);
            } else {
              console.log(recvData.msg_body.msg || "System Exception");
            }
          }
        };
    }
    registerCallBack(socketType, callBack) {
        this.callBackMapping[socketType] = callBack;
      }
      unRegisterCallBack(socketType) {
        this.callBackMapping[socketType] = null;
      }
      send(data) {
        if (this.connected) {
          this.sendRetryCounnt = 0;
          this.ws?.send(JSON.stringify(data));
        } else {
          this.sendRetryCounnt++;
          setTimeout(() => {
            this.send(data);
          }, this.sendRetryCounnt * 500);
        }
      }
      onClose() {
        this.ws?.close();
        this.manualClose = true;
      }
      // 心跳监测
      heartCheck() {
        this.heartReset();
        this.heartStart();
      }
    
      heartStart() {
        if (this.manualClose) return; // 不再重连就不再执行心跳
        this.pingTimeoutId = setTimeout(() => {
          // 这里发送一个心跳，后端收到后，返回一个心跳消息，
          // 拿到返回的心跳就说明连接正常
          this.send(this.pingMsg);
          // 如果超过一定时间还没重置，说明后端主动断开了
          this.pongTimeoutId = setTimeout(() => {
            // 如果会执行，我们执行ws.close()就行了.如果直接执行会触发导致重连两次
            this.onClose();
          }, this.pongTimeout);
        }, this.pingTimeout);
      }
    
      heartReset() {
        clearTimeout(this.pingTimeoutId);
        clearTimeout(this.pongTimeoutId);
      }
}