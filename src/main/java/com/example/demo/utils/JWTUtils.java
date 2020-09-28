package com.example.demo.utils;

import java.util.Calendar;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

public class JWTUtils {

	private static final String SING = "123456";// 私钥，通常会设置得很复杂。这里是学习练手，便没那么讲究

	/**
	 * 生成token header.payload.sing
	 */
	public static String getToken(Map<String, String> map, Integer id) {
		// 设置令牌token过期时间
		Calendar instance = Calendar.getInstance();
		instance.add(Calendar.DATE, 7);// 默认7天过期

		// 创建jwt builder
		JWTCreator.Builder builder = JWT.create();

		// payload
		// 遍历提供的键值对
		map.forEach((k, v) -> {
			builder.withClaim(k, v);
		});
		
		builder.withClaim("userID", id);

		String token = builder.withExpiresAt(instance.getTime())// 指定令牌过期时间
				.sign(Algorithm.HMAC256(SING));// sign

		return token;
	}

	/**
	 * 验证token 合法性
	 *
	 */
	public static DecodedJWT verify(String token) {

		return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
	}

	/**
	 * 获取token信息方法
	 *//*
		 * public static DecodedJWT getTokenInfo(String token){ DecodedJWT verify =
		 * JWT.require(Algorithm.HMAC256(SING)).build().verify(token); return verify; }
		 */

}
