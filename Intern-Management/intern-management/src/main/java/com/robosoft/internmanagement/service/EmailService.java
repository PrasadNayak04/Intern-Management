package com.robosoft.internmanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class EmailService
{

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public boolean sendEmail(String toEmail)
    {

        boolean flag = false;

        String host = "smtp.gmail.com";
        String subject = "OTP from Intern Management";

        Random random = new Random();
        int otp;

        do
        {
            otp = random.nextInt(9999);
        }
        while(String.valueOf(otp).length() < 3);


        String message = "Please use OTP " + otp + " for your account password reset request";

        try
        {

            SimpleMailMessage mailMessage = new SimpleMailMessage();

            mailMessage.setFrom(sender);
            mailMessage.setTo(toEmail);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);

            javaMailSender.send(mailMessage);
            String OTP=String.valueOf(otp);
            try
            {
                jdbcTemplate.queryForObject("select otp from user where emailId=?", String.class,toEmail);
                jdbcTemplate.update("update user set otp=?,expireTime=? where emailId=?",OTP,(System.currentTimeMillis()/1000)+120,toEmail);
            }
            catch (Exception e)
            {
                insert(toEmail,OTP);
            }
            flag = true;

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        finally
        {
            return flag;
        }

    }

    public void insert(String emailId,String code)
    {
        jdbcTemplate.update("insert into forgotPassword values (?,?,?,?)",emailId,null,code,(System.currentTimeMillis()/1000)+120);
    }


    public String verification(String emailId,String otp)
    {
        String verify=jdbcTemplate.queryForObject("select otp from forgotPassword where emailId=?", String.class,emailId);
        System.out.println(verify);
        System.out.println(otp);
        if(otp.equals(verify))
        {
            return "Done";
        }
        return "Invalid OTP";
    }
}
