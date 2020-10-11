package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.example.demo.email.SendEmail;
import com.example.demo.entity.User;
import com.example.demo.utils.CodeUtils;
import com.example.demo.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import com.aliyuncs.exceptions.ClientException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.demo.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

	@Autowired
	public UserService userService;
	private String code;//手机短信验证码
	private String phoneNum;//记录接收验证码的手机号
	@Autowired
	private SendEmail send;//不能随便new新的对象，容易报空指针错误

//	作为网盘的使用者，我希望用户可以使用已经分配的账户（手机号码）和密码进行登录，并获得相应使用权限，以使得我可以进行后续的管理操作。
	@PostMapping("/login")
	public Map<Object, Object> login(User user){

		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			User userDB = userService.login(user);
			map.put("msg", "登录成功！");
			map.put("Id", userDB.getId());//打印ID
			//payload是JWT的第二部分——信息搭载部分，一般不放敏感信息
			Map<String,String> payload =  new HashMap<>();
            payload.put("userID",userDB.getId());
            //生成JWT的令牌
            String token = JWTUtils.getToken(payload);
            map.put("token",token);//响应token
			//修改用户状态为True
			userDB.setState(true);
			try {
				userService.setState(userDB);
				System.out.println("设置用户状态成功！");
			}catch (Exception e){
				userDB.setState(false);
				userService.setState(userDB);
				System.out.println("设置用户状态失败！");
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("msg", "登录失败！");
			e.printStackTrace();
		}
		return map;
	}

//	作为网盘的使用者，我希望管理员用户可以使用已经分配的账户（手机号码）和密码进行登录，并获得相应使用权限，以使得我可以进行后续的管理操作。
	@PostMapping("/adminLogin")
	public Map<Object, Object> adminLogin(String pwd, String id){

		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			User userDB = userService.adminLogin(pwd,id);
			map.put("msg", "管理员登录成功！");
			map.put("Id", userDB.getId());//打印ID
			//payload是JWT的第二部分——信息搭载部分，一般不放敏感信息
			Map<String,String> payload =  new HashMap<>();
			payload.put("userID",userDB.getId());
			//生成JWT的令牌
			String token = JWTUtils.getToken(payload);
			map.put("token",token);//响应token
			//修改用户状态为True
			userDB.setState(true);
			try {
				userService.setState(userDB);
				System.out.println("设置用户状态成功！");
			}catch (Exception e){
				userDB.setState(false);
				userService.setState(userDB);
				System.out.println("设置用户状态失败！");
				e.printStackTrace();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			map.put("msg", "管理员登录失败！");
			e.printStackTrace();
		}
		return map;
	}

//	作为网盘的使用者，我希望用户登录后可以注销登录，跳转到安全页面，并不再能够访问需登录授权资源。
	@PostMapping("/logout")
	public Map<Object, Object> logout(HttpServletRequest request){

		Map<Object, Object> map = new HashMap<Object, Object>();
		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
		String Id = verify.getClaim("userID").asString();

		User userDB = userService.check(Id);
		if (userDB.getState()) {
			userDB.setState(false);
			try {
				userService.setState(userDB);
				map.put("msg", "注销成功！");
			} catch (SignatureVerificationException e) {
				e.printStackTrace();
				map.put("msg", "注销失败！");
			}
		}else {
			map.put("msg", "您已是注销状态！");
		}
		return map;
	}

//	作为网盘的使用者，我希望可以在登录状态中，通过输入新密码的方式变更自己的密码，以使得我下次登陆时可以使用新密码登录，旧密码失效。
	@PostMapping("/changePwd")
	public Map<Object, Object> changePwd(HttpServletRequest request, String oldPassword, String newPassword){

		Map<Object, Object> map = new HashMap<>();
        String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
        String Id = verify.getClaim("userID").asString();
		User userDB = userService.check(Id);
        if (userDB.getState()) {

			if (oldPassword.equals(userDB.getPwd())) {
				userDB.setPwd(newPassword);
				if (userService.changePwd(userDB)) {
					map.put("msg", "修改密码成功！");
				} else {
					map.put("msg", "修改密码失败！");
				}
			} else {
				map.put("msg", "输入的旧密码不正确！");
			}
		}else {
			map.put("msg", "请先登录！");
		}
        return map;
	}
	//发送手机验证码接口
	@PostMapping("/sendVerifyCode")
	public Map<Object, Object> sendVerifyCode(String phone) throws ClientException {

		Map<Object, Object> map = new HashMap<>();
		phoneNum = phone;
		//生成验证码
		CodeUtils Code = new CodeUtils();
		code = Code.getRandonString(4);
		System.out.println("验证码为"+code);
        //发送验证码
		SendSmsController sendCode = new SendSmsController();
		try {
			sendCode.sendSms(phone,code);
			map.put("msg", "发送手机验证码成功！");
		}catch (Exception e){
			map.put("msg", "发送手机验证码失败！");
		}
		return map;
	}

	//登记手机号
	@PostMapping("/setPhone")
	public Map<Object, Object> setPhone(HttpServletRequest request, String phone){

		Map<Object, Object> map = new HashMap<Object, Object>();
		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
		String Id = verify.getClaim("userID").asString();

		User userDB = userService.check(Id);
		if (userDB.getState()) {

			userDB.setPhone(phone);
			if (userService.setPhone(userDB)) {
				map.put("msg", "登记手机号成功！");
			} else {
				map.put("msg", "登记手机号失败！");
			}
		}else {
			map.put("msg", "请先登录！");
		}
		return map;
	}

//	作为网盘的使用者，我希望作为新增加的用户，在首次登陆系统时，可以通过登记的手机号码获得验证码的方式，进行密码设置，以使得该用户以后可以使用该密码进行登录，并获得相应的使用权限。
	@PostMapping("/setPwd")
	public Map<Object, Object> setPwd(HttpServletRequest request, String newPwd, String gotCode){

		Map<Object, Object> map = new HashMap<Object, Object>();
		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
		String Id = verify.getClaim("userID").asString();

		User userDB = userService.check(Id);
		if (userDB.getState()) {

			//校验手机验证码
			if (gotCode.contentEquals(code)) {
				userDB.setPwd(newPwd);
				if (userService.setPwd(userDB)) {
					map.put("msg", "新密码设置成功！");
				} else {
					map.put("msg", "新密码设置失败！");
				}
			} else {
				map.put("msg", "您所输入的验证码不正确，请重新输入！");
			}
		}else {
			map.put("msg", "请先登录！");
		}
		return map;
	}

//	作为网盘使用者，我希望用户可以通过账户信息的手机号以验证码的方式重设密码，以使得该用户可以使用新密码登录系统
	@PostMapping("/resetPwd")
	public Map<Object, Object> resetPwd(String newPwd01, String newPwd02, String gotCode){

		Map<Object, Object> map = new HashMap<Object, Object>();
		try {
			//查验数据库中有无当前接收验证码的手机号用户
			User userDB = userService.checkOnPhone(phoneNum);
			//检查两次输入的新密码是否一致
			if (newPwd01.equals(newPwd02)) {
				//校验验证码
				if (gotCode.equals(code)) {

					userDB.setPwd(newPwd01);//设置新的密码
					userDB.setState(true);//用户状态设置为登录状态
					if (userService.setPwd(userDB)) {
						map.put("msg", "重置密码成功！");
					} else {
						map.put("msg", "重置密码失败！");
					}

				} else {
					map.put("msg", "您所输入的验证码不正确，请重新输入！");
				}

			} else {
				map.put("msg", "两次输入的密码不一致，请重新输入！");
			}
		}catch (Exception e){
			e.printStackTrace();
			map.put("msg", "数据库中无此用户，请检查填写的手机号！");
		}
		return map;
	}

//	作为平台的使用者，我希望用户可以在登录状态中，通过手机验证码方式修改认证所需的手机号码，使得新号码作为个人的手机认证号码。
	@PostMapping("/changePhone")
	public Map<Object, Object> changePhone(HttpServletRequest request, String newPhone, String gotCode) {

		Map<Object, Object> map = new HashMap<Object, Object>();
		String token = request.getHeader("token");
        DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
        String Id = verify.getClaim("userID").asString();

		User userDB = userService.check(Id);
		if (userDB.getState()) {
			//校验手机验证码
			if (gotCode.contentEquals(code)) {
				userDB.setPhone(newPhone);//设置新的手机号
				if (userService.changePhone(userDB)) {
					map.put("msg", "修改手机号成功！");
				} else {
					map.put("msg", "修改手机号失败！");
				}

			} else {
				map.put("msg", "您所输入的验证码不正确，请重新输入！");
			}
		}else {
			map.put("msg", "请先登录！");
		}
		return map;
	}

//	只对管理员开放的功能
    @PostMapping("/adminManage")
	public void adminManage(HttpServletRequest request){

		String token = request.getHeader("token");
		DecodedJWT verify = JWTUtils.verify(token);
		//从token中获取信息
		String Id = verify.getClaim("userID").asString();

		User userDB = userService.check(Id);
		if (userDB.getState()) {
			if(userDB.getAdministrators()){
				System.out.println("这是管理员才可以看到的信息。");
			}else {
				System.out.println("读不起，您不是管理员！");
			}
		}else {
			System.out.println("请先登录！");
		}
	}

//	给用户发送邮箱
	@PostMapping("/sendEmail")
	public Map<Object, Object> sendEmail(String email) throws MessagingException {

		Map<Object, Object> map = new HashMap<Object, Object>();
		try{
			send.sendMsg(email);
			map.put("msg", "发送邮箱验证码成功！");
		}catch (Exception e){
			map.put("msg", "发送邮箱验证码失败！");
		}
		return map;
	}

}
