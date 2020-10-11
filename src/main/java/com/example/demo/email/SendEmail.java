package com.example.demo.email;

import com.example.demo.utils.CodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class SendEmail {

    @Autowired
    private JavaMailSender mailSender;

    public String code;//邮箱验证码

    public void sendMsg(String email) throws MessagingException {

        //生成验证码
        CodeUtils Code = new CodeUtils();
        code = Code.getRandonString(4);
        System.out.println(code);

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setSubject("验证码");//邮件主题
        helper.setText("<h1>"+code+"</h1>",true);//邮件内容
        helper.setTo(email);//接收方
        helper.setFrom("2814517003@qq.com");

        mailSender.send(mimeMessage);
    }

}
