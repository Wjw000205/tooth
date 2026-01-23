package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "文件元信息")
public class FileMeta {
    @Schema(description = "原始文件名（用于保留后缀）", example = "a.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    private String filename;

    @Schema(description = "内容类型", example = "image/jpeg")
    private String contentType;
}