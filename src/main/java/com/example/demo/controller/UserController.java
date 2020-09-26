package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import com.example.demo.utils.CodeUtils;
import com.example.demo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import com.aliyuncs.exceptions.ClientException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	private String code;
	@PostMapping("/login")
	public Map<Object, Object> login(User user){

		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			User userDB = userService.login(user);
			map.put("msg", "登录成功！");
			map.put("pwd", userDB.getPwd());//存储key和value
			map.put("state", userDB.getState());
			Integer ID = userDB.getId();
			Map<String,String> payload =  new HashMap<>();
            payload.put("pwd",userDB.getPwd());
            //payload.put("name",userDB.getAccount());
            //生成JWT的令牌
            String token = JWTUtils.getToken(payload,ID);
            map.put("token",token);//响应token	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//map.put("state", false);
			map.put("msg", "controller——登录失败！");
			e.printStackTrace();
		}
		return map;
	}
	
	@PostMapping("/changePwd") 
	public Map<Object, Object> changePwd(HttpServletRequest request, String oldPassword, String newPassword){
			 
		Map<Object, Object> map = new HashMap<>();
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        //String Account = verify.getClaim("name").asString();
        String oldPwd  = verify.getClaim("pwd").asString();
        Integer Id = verify.getClaim("userID").asInt();
        //System.out.println(Id);
		
        if (oldPassword.equals(oldPwd)) {
        	User user = new User(); 
    		user.setPwd(newPassword);
    		user.setId(Id);
    		//user.setAccount(Account);
    		user.setState(true);
    		if (userService.changePwd(user)) {
				System.out.println("修改密码成功！");
			}else {
				System.out.println("controller——修改密码失败！");
			}
		}else {
			System.out.println("输入的旧密码不正确！");
		}
        
        return map;
	}
	
	@PostMapping("/sendVerifyCode")
	public void sendVerifyCode(String phone) throws ClientException {
		
		//生成验证码
        CodeUtils Code = new CodeUtils();
        code = Code.getRandonString(4);
		System.out.println(code);
        //发送验证码
        SendSmsController sendCode = new SendSmsController();
        sendCode.sendSms(phone, code);	
	}

	@PostMapping("/setPhone")
	public void setPhone(HttpServletRequest request, String phone){

		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		String Pwd  = verify.getClaim("pwd").asString();
		Integer Id = verify.getClaim("userID").asInt();

		User user = new User();
		user.setPhone(phone);
		user.setPwd(Pwd);//设置新的密码
		user.setId(Id);
		user.setState(true);//用户状态不允许为空，所以也需要设置
		if (userService.setPhone(user)) {
			System.out.println("登记手机号成功！");
		}else {
			System.out.println("controller——登记手机号失败！");
		}
	}


	@PostMapping("/setPwd")
	public void setPwd(HttpServletRequest request, String newPwd, String gotCode){

		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		String oldPwd  = verify.getClaim("pwd").asString();
		Integer Id = verify.getClaim("userID").asInt();

		//校验手机验证码
		if (gotCode.contentEquals(code)) {

			User user = new User();
			user.setPwd(newPwd);//设置新的密码
			user.setId(Id);
			user.setState(true);//用户状态不允许为空，所以也需要设置

			if (userService.setPwd(user)) {
				System.out.println("新密码设置成功！");
			}else {
				System.out.println("controller——新密码设置失败！");
			}

		}else {
			System.out.println("您所输入的验证码不正确，请重新输入。");
		}
	}

	
	@PostMapping("/changePhone") 
	public void changePhone(HttpServletRequest request, String newPhone, String gotCode) {
		String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
        String oldPwd  = verify.getClaim("pwd").asString();
        Integer Id = verify.getClaim("userID").asInt();
        //校验手机验证码
        if (gotCode.contentEquals(code)) {
        	
        	User user = new User(); 
    		user.setPwd(oldPwd);//密码不允许为空，所以需要设置
    		user.setId(Id);
    		user.setState(true);//用户状态不允许为空，所以也需要设置
    		user.setPhone(newPhone);//设置新的手机号
    		if (userService.changePhone(user)) {
    			System.out.println("修改手机号成功！");
    		}else {
    			System.out.println("controller——修改手机号失败！");
    		}
			
		}else {
			System.out.println("您所输入的验证码不正确，请重新输入。");
		}
     
	}

}
