package org.example.tooth.Dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.tooth.Entity.UserEntity;

@Mapper
public interface UserDao extends BaseMapper<UserEntity> {
}
