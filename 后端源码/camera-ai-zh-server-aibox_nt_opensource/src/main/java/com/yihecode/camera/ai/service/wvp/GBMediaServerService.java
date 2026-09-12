package com.yihecode.camera.ai.service.wvp;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yihecode.camera.ai.entity.MediaServer;
import com.yihecode.camera.ai.entity.wvp.GBMediaServer;

/**
* GB Standard Media Node Management
*/
public interface GBMediaServerService extends IService<GBMediaServer> {

    /**
* Add Node
* @param mediaServer
*/
    void saveData(MediaServer mediaServer);
}
