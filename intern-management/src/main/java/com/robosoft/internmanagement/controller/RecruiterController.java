package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.MemberService;
import com.robosoft.internmanagement.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecruiterController
{
    @Autowired
    EmailService emailService;

    @Autowired
    RecruiterService recruiterService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendMail(@RequestParam String toEmail){
        boolean mailSent = emailService.sendEmail(toEmail);

        if(mailSent){
            return ResponseEntity.ok().body("Otp has been sent to the email \"" + toEmail + "\"");
        }else{
            return ResponseEntity.status(HttpStatus.valueOf("Please provide valid email.")).build();
        }
    }

    @PutMapping("/verify-otp")
    public String verify(@RequestParam String emailId,@RequestParam String otp)
    {
        return emailService.verification(emailId,otp);
    }

    @GetMapping("/organizer")
    public List<Organizer> getList()
    {
        return recruiterService.getOrganizer();
    }

    @GetMapping("/summary")
    public Summary getSummary()
    {
        return recruiterService.getSummary();
    }

    @GetMapping("/cv-count")
    public int getCvCount()
    {
        return recruiterService.cvCount();
    }

    @GetMapping("/logged-profile")
    public LoggedProfile getProfile()
    {
        return recruiterService.getProfile();
    }

    @GetMapping("/notification-display")
    public NotificationDisplay getNotifications()
    {
        return recruiterService.notification();
    }

    @GetMapping("/cv-analysis")
    public List<CvAnalysis> getCv()
    {
        return recruiterService.cvAnalysisPage();
    }

    @GetMapping("/search/{designation}")
    public CvAnalysis search(@PathVariable String designation)
    {
        return recruiterService.searchDesignation(designation);
    }

    @GetMapping("/get-applicants")
    public List<Applications> getApplicants()
    {
        return recruiterService.getNotAssignedApplicants();
    }

    @PutMapping("/assign-organizer")
    public String setOrganizer(@ModelAttribute AssignBoard assignBoard)
    {
        System.out.println(MemberService.getCurrentUser());
        return recruiterService.assignOrganizer(assignBoard);
    }

}
