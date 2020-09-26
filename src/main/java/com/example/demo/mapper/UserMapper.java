package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    User login(User user);//登录

    User check(int id);//根据ID查询数据库

    Boolean changePwd(User user);//登录状态下改密码

    Boolean setPwd(User user);//登录状态下手机验证码方式设置密码

    Boolean setPhone(User user);//登记手机号

    Boolean changePhone(User user);//登录状态下改手机号

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}