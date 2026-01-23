package org.example.tooth.Service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import org.example.tooth.Dao.MarkDao;
import org.example.tooth.Entity.MarkEntity;
import org.example.tooth.Service.MarkService;
import org.example.tooth.DTO.FileMeta;
import org.example.tooth.DTO.PreSignUploadRespItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class MarkServiceImp extends ServiceImpl<MarkDao, MarkEntity> implements MarkService {

    private final MinioClient minioClient;
    private static final String BUCKET_NAME = "tooth";

    public MarkServiceImp(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

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
}
