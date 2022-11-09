package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Usercontroller
{
    @Autowired
    EmailService emailService;


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailService.sendEmail(toEmail);

        if(mailSent){
            return ResponseEntity.ok().body("Mail has been sent to the email \"" + toEmail + "\"");
        }else{
            return ResponseEntity.status(HttpStatus.valueOf("Please provide valid email.")).build();
        }
    }

    @PutMapping("/verify-otp")
    public String verify(@RequestParam String emailid,@RequestParam String otp)
    {
        return emailService.verification(emailid,otp);
    }
}
