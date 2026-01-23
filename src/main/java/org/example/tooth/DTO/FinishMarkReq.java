package org.example.tooth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@Schema(name = "FinishMarkReq", description = "提交标注结果：上传xml并完成入库")
public class FinishMarkReq {

    @Schema(description = "标注xml文件", type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile xmlFile;

    @Schema(description = "图片记录ID（mark表主键id）", example = "12", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pictureId;

    @Schema(description = "标注人用户ID", example = "1001", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer userId;
}
