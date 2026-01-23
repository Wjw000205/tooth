package org.example.tooth.Service;

import org.example.tooth.DTO.FileMeta;
import org.example.tooth.DTO.FinishMarkReq;
import org.example.tooth.DTO.MarkItemDTO;
import org.example.tooth.DTO.PreSignUploadRespItem;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MarkService {

    // 1) 生成预签名PUT URL（前端直传）
    List<PreSignUploadRespItem> preSignUpload(List<FileMeta> files, int uploader);

    // 2) 直传成功后确认入库
    int confirmUpload(List<String> objectNames, int uploader);

    //获取当前用户需要标注的图片的链接列表
    List<MarkItemDTO> getMarkList(int userId);

    boolean finishMark(FinishMarkReq req);
}

