package org.example.tooth.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.example.tooth.DTO.FinishMarkReq;
import org.example.tooth.Service.MarkService;
import org.example.tooth.DTO.ConfirmUploadReq;
import org.example.tooth.DTO.PreSignUploadReq;
import org.example.tooth.DTO.PreSignUploadRespItem;
import org.example.tooth.common.utils.R;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @Operation(summary = "根据用户ID获取需要标注的图片", description = "路径后跟上用户ID，获取当前用户需要进行标注的图片列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回图片列表",
                    content = @Content(schema = @Schema(implementation = R.class)))
    })
    @GetMapping(value = "/getPictureList/{userId}")
    public R confirmUpload(@PathVariable int userId) {
        List<String> pictureList = markService.getMarkList(userId);
        return R.ok("获取成功").put("pictureList", pictureList);
    }

    @Operation(summary = "提交标注结果并入库", description = "上传xml到MinIO，并更新mark表：state=2，填mark_file_name与finish_time")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回提交结果",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = R.class)))
    })
    @PostMapping(value = "/finishMark", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public R finishMark(@ModelAttribute FinishMarkReq req) {
        if (req == null) return R.error("参数不能为空");
        if (req.getXmlFile() == null || req.getXmlFile().isEmpty()) return R.error("xml文件不能为空");
        if (req.getPictureId() == null) return R.error("pictureId不能为空");
        if (req.getUserId() == null) return R.error("userId不能为空");

        boolean ok = markService.finishMark(req);
        return ok ? R.ok("提交成功") : R.error("提交失败");
    }
}

