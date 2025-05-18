package com.akhil.authify.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendWelcomeEmail(String toEmail,String name){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Welcome to Our PlatForm");
        message.setText("Hello " + name + ",\n\n Thanks for registering with us !\n\n Regards,\n Akhil ");
        mailSender.send(message);
    }

//    public void sendResetOtpEmail(String toEmail,String otp){
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);
//        message.setTo(toEmail);
//        message.setSubject("Password Reset OTP");
//        message.setText("Your OTP for resetting your password is " + otp+". Use this otp to proceed with resetting your password.");
//        mailSender.send(message);
//    }
//
//    public void sendOtpEmail(String toEmail,String otp){
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(fromEmail);
//        message.setTo(toEmail);
//        message.setSubject("Account verification OTP");
//        message.setText("Your OTP  is " + otp+". Verify your account using this OTP.");
//        mailSender.send(message);
//    }

    public void sendResetOtpEmail(String toEmail,String otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("email", toEmail);
        String passwordResetEmail = templateEngine.process("password-reset-email", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject("Forgot your password");
        mimeMessageHelper.setText(passwordResetEmail, true);
        mailSender.send(mimeMessage);

    }

    public void sendOtpEmail(String toEmail,String otp) throws MessagingException {
        Context context = new Context();
        context.setVariable("otp", otp);
        context.setVariable("email", toEmail);
        String verifyEmail = templateEngine.process("verify-email", context);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        mimeMessageHelper.setFrom(fromEmail);
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setSubject("Account Verification OTP");
        mimeMessageHelper.setText(verifyEmail, true);
        mailSender.send(mimeMessage);

    }
}
