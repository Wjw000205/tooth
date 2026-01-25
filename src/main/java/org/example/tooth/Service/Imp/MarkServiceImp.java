package org.example.tooth.Service.Imp;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.example.tooth.DTO.FinishMarkReq;
import org.example.tooth.DTO.MarkItemDTO;
import org.example.tooth.Dao.MarkDao;
import org.example.tooth.Entity.MarkEntity;
import org.example.tooth.Service.MarkService;
import org.example.tooth.DTO.FileMeta;
import org.example.tooth.DTO.PreSignUploadRespItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MarkServiceImp extends ServiceImpl<MarkDao, MarkEntity> implements MarkService {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "tooth";

    @Override
    public List<PreSignUploadRespItem> preSignUpload(List<FileMeta> files, int uploader) {
        if (files == null || files.isEmpty()) return Collections.emptyList();

        try {
            // bucket 一次性检查
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            List<PreSignUploadRespItem> res = new ArrayList<>(files.size());

            for (FileMeta f : files) {
                if (f == null || f.getFilename() == null || f.getFilename().isBlank()) continue;

                String filename = f.getFilename();
                String suffix = "";
                int idx = filename.lastIndexOf('.');
                if (idx >= 0) suffix = filename.substring(idx);

                // objectName 可加目录：uploader/xxx.jpg
                String objectName = uploader + "/" + UUID.randomUUID() + suffix;

                String putUrl = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(io.minio.http.Method.PUT)
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .expiry(15 * 60) // 15分钟
                                .build()
                );

                PreSignUploadRespItem item = new PreSignUploadRespItem();
                item.setFilename(filename);
                item.setObjectName(objectName);
                item.setPutUrl(putUrl);
                res.add(item);
            }

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    @Override
    public int confirmUpload(List<String> objectNames, int uploader) {
        if (objectNames == null || objectNames.isEmpty()) return 0;

        int success = 0;

        for (String objectName : objectNames) {
            if (objectName == null || objectName.isBlank()) continue;

            try {
                // 可选：校验对象确实存在，防止“伪确认”
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .build()
                );

                MarkEntity mark = new MarkEntity();
                mark.setPictureName(objectName);
                mark.setUploader(uploader);
                mark.setState(0);
                mark.setUploadTime(java.time.LocalDateTime.now());

                if (this.baseMapper.insert(mark) == 1) success++;
            } catch (Exception e) {
                // statObject 失败 or insert 失败：跳过即可
                e.printStackTrace();
            }
        }

        return success;
    }

    @Override
    public List<MarkItemDTO> getMarkList(int userId) {
        if (userId <= 0) return Collections.emptyList();

        List<MarkEntity> list = this.baseMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<MarkEntity>()
                        .eq(MarkEntity::getMarker, userId)
                        .eq(MarkEntity::getState, 1)
                        .select(MarkEntity::getId, MarkEntity::getPictureName)
                        .orderByAsc(MarkEntity::getDistributeTime)
        );

        if (list == null || list.isEmpty()) return Collections.emptyList();

        List<MarkItemDTO> res = new ArrayList<>(list.size());

        for (MarkEntity m : list) {
            if (m == null || m.getId() == null) continue;
            String objectName = m.getPictureName();
            if (objectName == null || objectName.isBlank()) continue;

            MarkItemDTO dto = new MarkItemDTO();
            dto.setId(m.getId());
            dto.setPictureName(objectName);

            try {
                // 预览用：不要加 attachment
                String url = minioClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(io.minio.http.Method.GET)
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .expiry(30 * 60) // 30分钟（秒）
                                .build()
                );
                dto.setUrl(url);
            } catch (Exception e) {
                e.printStackTrace();
                dto.setUrl(null);
            }

            res.add(dto);
        }

        return res;
    }



    @Override
    public boolean finishMark(FinishMarkReq req) {
        MultipartFile file = req.getMarkFile(); // 字段名不改也能用
        int pictureId = req.getPictureId();
        int userId = req.getUserId();

        if (file == null || file.isEmpty()) return false;

        try {
            // 1) 确保 bucket 存在
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(BUCKET_NAME).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(BUCKET_NAME).build());
            }

            // 2) 生成 objectName：保留原始后缀（可为空）
            String original = file.getOriginalFilename();
            String suffix = "";
            if (original != null) {
                int dot = original.lastIndexOf('.');
                if (dot >= 0 && dot < original.length() - 1) {
                    suffix = original.substring(dot); // .xml/.json/.zip...
                }
            }
            String objectName = "markfile/" + userId + "/" + pictureId + "/" + UUID.randomUUID() + suffix;

            // 3) 上传到 MinIO（contentType 自动获取）
            String contentType = file.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            try (InputStream in = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET_NAME)
                                .object(objectName)
                                .stream(in, file.getSize(), -1)
                                .contentType(contentType)
                                .build()
                );
            }

            // 4) 更新 mark 表（state=2 + mark_file_name + finish_time）
            LocalDateTime now = LocalDateTime.now();
            int rows = this.baseMapper.update(
                    null,
                    new LambdaUpdateWrapper<MarkEntity>()
                            .eq(MarkEntity::getId, pictureId)
                            .eq(MarkEntity::getMarker, userId)
                            .eq(MarkEntity::getState, 1)
                            .set(MarkEntity::getState, 2)
                            .set(MarkEntity::getMarkFileName, objectName)
                            .set(MarkEntity::getFinishTime, now)
            );

            if (rows == 1) return true;

            // 5) DB 未更新：回滚删除 MinIO 对象
            try {
                minioClient.removeObject(RemoveObjectArgs.builder()
                        .bucket(BUCKET_NAME)
                        .object(objectName)
                        .build());
            } catch (Exception ignore) {}

            return false;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
