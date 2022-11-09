package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/intern-management")
public class EmailController {
    @Autowired
    private EmailService emailService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailService.sendMail(toEmail);

        if(mailSent){
            return ResponseEntity.ok().body("Mail has been sent to the email \"" + toEmail + "\"");
        }else{
           return ResponseEntity.status(HttpStatus.valueOf("Please provide valid email.")).build();
        }
    }
}
