package com.yihecode.camera.ai.netty.data;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
public class RestartResponse extends Response {

    private boolean status;

    private String msg;

}
