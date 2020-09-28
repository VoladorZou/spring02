package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
	
	User login(User user);
	User check(int id);
	User checkOnPhone(String phone);
	Boolean setState(User user);
	Boolean changePwd(User user);
	Boolean changePhone(User user);
	Boolean setPwd(User user);
	Boolean setPhone(User user);

}
