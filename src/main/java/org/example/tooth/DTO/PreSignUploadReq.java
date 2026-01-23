package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量预签名上传请求")
public class PreSignUploadReq {
    @Schema(description = "上传者用户ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer uploader;

    @Schema(description = "文件元信息列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FileMeta> files;
}