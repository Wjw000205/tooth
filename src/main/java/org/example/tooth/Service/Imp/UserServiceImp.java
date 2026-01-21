package org.example.tooth.Service.Imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.tooth.Dao.UserDao;
import org.example.tooth.Entity.UserEntity;
import org.example.tooth.Service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp extends ServiceImpl<UserDao, UserEntity> implements UserService{

}
