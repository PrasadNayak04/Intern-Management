package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

@RestController
@RequestMapping("/intern-management/recruiter")
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
    public List<Organizer> getList(@RequestParam String emailId)
    {
        return recruiterService.getOrganizer(emailId);
    }

    @GetMapping("/summary")
    public Summary getSummary(@RequestParam String emailId)
    {
        return recruiterService.getSummary(emailId);
    }

    @GetMapping("/cv-count")
    public int getCv(@RequestParam String emailId)
    {
        return recruiterService.cvCount(emailId);
    }

    @GetMapping("/logged-profile")
    public LoggedProfile getProfile(@RequestParam String emailId)
    {
        return recruiterService.getProfile(emailId);
    }

    @GetMapping("/notification-display")
    public NotificationDisplay getNotifications(@RequestParam String emailId)
    {
        return recruiterService.notification(emailId);
    }

    @GetMapping("/cv-analysis")
    public List<CvAnalysis> getCv (@RequestParam(required = false) Date date)
    {
        return recruiterService.cvAnalysisPage(date);
    }

    @GetMapping("/search/{designation}")
    public CvAnalysis search(@PathVariable String designation)
    {
        return recruiterService.searchDesignation(designation);
    }

    @PostMapping("/update-position-status")
    public int updatePositionStatus(@RequestParam String designation, @RequestParam String newStatus){
        return recruiterService.updateStatus(designation, newStatus);
    }

    @GetMapping("/extended-cv/{emailId}")
    public ResponseEntity<?> getExtendedCV(@PathVariable String emailId){
        ExtendedCV extendedCV = recruiterService.getBasicCVDetails(emailId);
        if(extendedCV == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No candidate found with email " + emailId);
        }
        extendedCV.setEducations(recruiterService.getEducationsHistory(emailId));
        extendedCV.setWorkHistories(recruiterService.getWorkHistory(emailId));
        extendedCV.setLinks(recruiterService.getSocialLinks(emailId));
        return ResponseEntity.ok(extendedCV);
    }

    @GetMapping("/resume-url/{emailId}")
    public ResponseEntity<?> getResumeDownloadUrl(@PathVariable String emailId){
        String url = recruiterService.downloadCV(emailId);
        if(url.equals(null))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(url);
    }

}
