package org.example.tooth.Service;

import org.example.tooth.DTO.FileMeta;
import org.example.tooth.DTO.PreSignUploadRespItem;

import java.util.List;

public interface MarkService {

    // 1) 生成预签名PUT URL（前端直传）
    List<PreSignUploadRespItem> preSignUpload(List<FileMeta> files, int uploader);

    // 2) 直传成功后确认入库
    int confirmUpload(List<String> objectNames, int uploader);
}

