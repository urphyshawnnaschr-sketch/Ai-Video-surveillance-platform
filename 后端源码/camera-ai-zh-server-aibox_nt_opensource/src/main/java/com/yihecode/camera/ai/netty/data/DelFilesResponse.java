package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DelFilesResponse extends Response {

    private boolean status;

    private String msg;

}
