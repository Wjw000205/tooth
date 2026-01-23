package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预签名上传返回项")
public class PreSignUploadRespItem {
    @Schema(description = "原始文件名", example = "a.jpg")
    private String filename;

    @Schema(description = "MinIO对象名（入库用）", example = "c8c3f9b0-...-... .jpg")
    private String objectName;

    @Schema(description = "PUT预签名URL（前端用PUT直传）")
    private String putUrl;
}