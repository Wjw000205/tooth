package org.example.tooth.Service;

import com.alibaba.fastjson2.JSONObject;
import org.example.tooth.DTO.UserDTO;
import org.example.tooth.Entity.UserEntity;

public interface UserService {
    int register(String username, String password);

    int login(UserDTO req);
}
