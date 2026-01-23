package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "上传确认请求（前端直传成功后回调）")
public class ConfirmUploadReq {
    @Schema(description = "上传者用户ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer uploader;

    @Schema(description = "成功上传的objectName列表", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> objectNames;
}