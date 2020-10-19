package com.example.demo.controller;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.example.demo.utils.CodeUtils;
import com.google.gson.Gson;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 发送短信
 */
public class SendSmsController {

    // 设置公共请求参数，初始化Client。
    private DefaultProfile profile = DefaultProfile.getProfile(
          "cn-hangzhou",// API支持的地域ID，如短信API的值为：cn-hangzhou。
          "XXXXXXXXXXXXXXXXX",// 您的AccessKey ID。
          "XXXXXXXXXXXXXXXXXXXXXXXXXXXX");// 您的AccessKey Secret。
    private IAcsClient client = new DefaultAcsClient(profile);

    private static void log_print(String functionName, Object result) {
        Gson gson = new Gson();
        System.out.println("-------------------------------" + functionName + "-------------------------------");
        System.out.println(gson.toJson(result));
    }
    /**
     * 发送短信
     */
    public String sendSms(String phone, String code) throws ClientException {
    	
        CommonRequest request = new CommonRequest();
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        // 接收短信的手机号码
        request.putQueryParameter("PhoneNumbers", phone);
        // 短信签名名称。请在控制台签名管理页面签名名称一列查看（必须是已添加、并通过审核的短信签名）。
        request.putQueryParameter("SignName", "ABC商城");
        // 短信模板ID
        request.putQueryParameter("TemplateCode", "SMS_203181891");
        // 短信模板变量对应的实际值，JSON格式。
        request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
        request.putQueryParameter("SmsUpExtendCode", "90999");
        CommonResponse commonResponse = client.getCommonResponse(request);
        String data = commonResponse.getData();
        String sData = data.replaceAll("'\'", "");
        log_print("sendSms", sData);
        Gson gson = new Gson();
        Map map = gson.fromJson(sData, Map.class);
        Object bizId = map.get("BizId");
        return bizId.toString();
    }

    /**
     * 查询发送详情
     */
    public void querySendDetails(String bizId) throws ClientException {
        CommonRequest request = new CommonRequest();
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("QuerySendDetails");
        // 接收短信的手机号码
        request.putQueryParameter("PhoneNumber", "18420178854");
        // 短信发送日期，支持查询最近30天的记录。格式为yyyyMMdd，例如20191010。
        request.putQueryParameter("SendDate", "20200924");
        // 分页记录数量
        request.putQueryParameter("PageSize", "10");
        // 分页当前页码
        request.putQueryParameter("CurrentPage", "1");
        // 发送回执ID，即发送流水号。
        request.putQueryParameter("BizId", bizId);
        CommonResponse response = client.getCommonResponse(request);
        log_print("querySendDetails", response.getData());
    }
}
