package org.example.tooth.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.tooth.Service.MarkService;
import org.example.tooth.DTO.ConfirmUploadReq;
import org.example.tooth.DTO.PreSignUploadReq;
import org.example.tooth.DTO.PreSignUploadRespItem;
import org.example.tooth.common.utils.R;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
public class MarkController {

    private final MarkService markService;

    public MarkController(MarkService markService) {
        this.markService = markService;
    }

    @Operation(summary = "生成批量上传预签名URL", description = "后端生成PUT预签名URL，前端用PUT直传MinIO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回objectName与putUrl列表",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = R.class)))
    })
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R preSignUpload(@RequestBody PreSignUploadReq req) {
        if (req == null || req.getUploader() == null) return R.error("uploader不能为空");
        if (req.getFiles() == null || req.getFiles().isEmpty()) return R.error("files不能为空");

        List<PreSignUploadRespItem> items = markService.preSignUpload(req.getFiles(), req.getUploader());
        if (items == null || items.isEmpty()) return R.error("生成预签名失败");

        return R.ok("生成成功").put("items", items);
    }

    @Operation(summary = "确认上传并入库", description = "前端直传成功后回调，后端写入mark表（picture_name/uploader/state/upload_time）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回成功入库数量",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = R.class)))
    })
    @PostMapping(value = "/confirm", consumes = MediaType.APPLICATION_JSON_VALUE)
    public R confirm(@RequestBody ConfirmUploadReq req) {
        if (req == null || req.getUploader() == null) return R.error("uploader不能为空");
        if (req.getObjectNames() == null || req.getObjectNames().isEmpty()) return R.error("objectNames不能为空");

        int inserted = markService.confirmUpload(req.getObjectNames(), req.getUploader());
        if (inserted <= 0) return R.error("入库失败");

        return R.ok("入库成功").put("inserted", inserted);
    }
}

