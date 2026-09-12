package com.yihecode.camera.ai.web.dto;

import lombok.Data;
import org.eclipse.jgit.internal.storage.file.PackReverseIndex;

/**
* oss File in between Result
*/
@Data
public class OssFileDTO {

    private String filename;

    private long filesize;

    private String version;

    private double ver;
}
