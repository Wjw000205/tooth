package org.example.tooth.Service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.tooth.DTO.UserDTO;
import org.example.tooth.Dao.UserDao;
import org.example.tooth.Entity.UserEntity;
import org.example.tooth.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserServiceImp extends ServiceImpl<UserDao, UserEntity> implements UserService{
    private final UserDao userDao;
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
    public int login(UserDTO req) {
        if (req == null) return 0;

        String username = req.getUserName();
        String rawPassword = req.getPassword();

        if (username == null || username.trim().isEmpty()) return 0;
        if (rawPassword == null || rawPassword.isEmpty()) return 0;

        username = username.trim();

        // 1) 按用户名查用户（只取一条）
        UserEntity dbUser = this.baseMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUserName, username)
                        .last("limit 1")
        );
        if (dbUser == null) return 0;

        // 2) BCrypt 校验：matches(明文, hash)
        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        boolean ok = encoder.matches(rawPassword, dbUser.getPassword());
        return ok ? (dbUser.getId() == null ? 0 : dbUser.getId().intValue()) : 0;
    }
}
