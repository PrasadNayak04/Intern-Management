package com.robosoft.internmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendMail(String toEmail){

        boolean flag = false;

        String host = "smtp.gmail.com";
        String subject = "OTP from Intern Management";

        Random random = new Random();
        int otp;

        do {
            otp = random.nextInt(9999);
        } while(String.valueOf(otp).length() < 3);


        String message = "Please use OTP " + otp + " for your account password reset request";

        try {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            javaMailSender.send(mailMessage);

            flag = true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        finally{
            return flag;
        }

    }

}
