package com.example.demo.service.impl;

import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.service.UserService;
@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private UserMapper userMapper;

	@Override
	@Transactional(propagation = Propagation.SUPPORTS)
	public User login(User user) {
		// 根据接收的账号、密码查询数据库
		User userDB = userMapper.login(user);
		if (userDB!=null) {
			return userDB;
		}
		throw new RuntimeException("service——登录失败！");
	}

	@Override
	public User adminLogin(String pwd, String id) {
		// 根据接收的账号、密码查询数据库
		User userDB = userMapper.adminLogin(pwd,id);
		if (userDB!=null) {
			return userDB;
		}
		throw new RuntimeException("service——管理员登录失败！");
	}

	@Override
	public User check(String id) {
		// 根据接收的ID查询数据库
		User userDB = userMapper.check(id);
		if (userDB!=null) {
			return userDB;
		}
		throw new RuntimeException("service——查询失败！数据库无此用户");
	}

	@Override
	public User checkOnPhone(String phone) {
		// 根据接收的phoneNum查询数据库
		User userDB = userMapper.checkOnPhone(phone);
		if (userDB!=null) {
			return userDB;
		}
		throw new RuntimeException("service——查询失败！数据库无此用户");
	}

	@Override
	public Boolean setState(User user) {
		Boolean flag = userMapper.setState(user);
		if (flag==true) {
			System.out.println("userService——设置用户状态成功！");
			return true;
		}else {
			System.out.println("userService——设置用户状态失败！");
			return false;
		}
	}

	@Override
	public Boolean changePwd(User user) {
		// TODO Auto-generated method stub
		Boolean flag = userMapper.changePwd(user);
		if (flag==true) {
			System.out.println("userService——密码修改成功！");
			return true;
		}else {
			System.out.println("userService——密码修改失败！");
			return false;
		}
	}

	@Override
	public Boolean changePhone(User user) {
		// TODO Auto-generated method stub
		Boolean flag = userMapper.changePhone(user);
		if (flag==true) {
			System.out.println("userService——手机号修改成功！");
			return true;
		}else {
			System.out.println("userService——手机号修改失败！");
			return false;
		}
	}

	@Override
	public Boolean setPwd(User user) {
		Boolean flag = userMapper.setPwd(user);
		if (flag==true) {
			System.out.println("userService——新密码设置成功！");
			return true;
		}else {
			System.out.println("userService——新密码设置失败！");
			return false;
		}
	}

	@Override
	public Boolean setPhone(User user) {
		Boolean flag = userMapper.setPhone(user);
		if (flag==true) {
			System.out.println("userService——登记手机号成功！");
			return true;
		}else {
			System.out.println("userService——登记手机号失败！");
			return false;
		}
	}

}
