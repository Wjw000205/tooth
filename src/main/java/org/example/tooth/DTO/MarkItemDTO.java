package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "待标注图片条目")
public class MarkItemDTO {

    @Schema(description = "mark表主键ID", example = "12")
    private Integer id;

    @Schema(description = "图片对象名（MinIO objectName）", example = "1001/xxx.jpg")
    private String pictureName;
}
