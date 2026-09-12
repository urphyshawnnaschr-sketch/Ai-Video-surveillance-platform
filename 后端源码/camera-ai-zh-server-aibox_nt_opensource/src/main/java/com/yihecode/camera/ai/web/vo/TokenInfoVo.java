package com.yihecode.camera.ai.web.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
* token Info
*/
@ApiModel("token Info")
@Data
public class TokenInfoVo {

    /**
* token Name
*/
    private String tokenName;

    /**
* token Value
*/
    private String tokenValue;

    /**
* Speaker Pole Function can Whether Enable
*/
    private boolean soundColumnEnable;

    /**
* Record make Function can Whether Enable
*/
    private boolean recordEnable;
}
