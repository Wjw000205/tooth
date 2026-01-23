package org.example.tooth.Service.Imp;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.example.tooth.Dao.UserDao;
import org.example.tooth.Entity.UserEntity;
import org.example.tooth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp extends ServiceImpl<UserDao, UserEntity> implements UserService{
    @Autowired
    private UserDao userDao;
    @Override
    public int register(String username, String password) {
        if (username == null || username.trim().isEmpty()) return 0;
        if (password == null || password.isEmpty()) return 0;

        username = username.trim();

        // 1) 查重：用户名已存在则注册失败

        Long cnt = userDao.selectCount(new LambdaQueryWrapper<UserEntity>()
                .eq(UserEntity::getUserName, username));
        if (cnt != null && cnt > 0) return 2;

        // 2) 组装实体 + 密码加密（推荐 BCrypt）
        UserEntity user = new UserEntity();
        user.setUserName(username);
        user.setPassword(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode(password));
        user.setRole(0); // 默认角色：自行按你的业务改

        // 3) 插入
      if(userDao.insert(user) == 1)
          return 1;
      else return 0;
    }

    @Override
    public boolean login(UserEntity req) {
        if (req == null) return false;

        String username = req.getUserName();
        String rawPassword = req.getPassword();

        if (username == null || username.trim().isEmpty()) return false;
        if (rawPassword == null || rawPassword.isEmpty()) return false;

        username = username.trim();

        // 1) 按用户名查用户（只取一条）
        UserEntity dbUser = this.baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUserName, username)
                        .last("limit 1")
        );
        if (dbUser == null) return false;

        // 2) BCrypt 校验：用 matches(明文, 数据库存的hash)
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        return encoder.matches(rawPassword, dbUser.getPassword());
    }
}
