package org.example.tooth.Entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("mark")
@Schema(name = "MarkEntity", description = "图片标注任务/记录")
public class MarkEntity {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @TableField("picture_name")
    @Schema(description = "图片文件名", example = "a1b2c3.jpg")
    private String pictureName;

    @TableField("uploader")
    @Schema(description = "上传图片的人的ID", example = "1001")
    private Integer uploader;

    @TableField("marker")
    @Schema(description = "负责标注的人的ID", example = "2001")
    private Integer marker;

    @TableField("state")
    @Schema(description = "图片状态（0：已上传，1：已分发，2：已完成标注）", example = "0")
    private Integer state;

    @TableField("upload_time")
    @Schema(description = "图片上传时间", example = "2026-01-23T10:30:00")
    private LocalDateTime uploadTime;

    @TableField("finish_time")
    @Schema(description = "完成标注时间", example = "2026-01-23T12:00:00")
    private LocalDateTime finishTime;

    @TableField("distribute_time")
    @Schema(description = "分配时间", example = "2026-01-23T11:00:00")
    private LocalDateTime distributeTime;

    @TableField("mark_file_name")
    @Schema(description = "标注的xml文件名（完成标注后才有）", example = "a1b2c3.xml", nullable = true)
    private String markFileName;
}
