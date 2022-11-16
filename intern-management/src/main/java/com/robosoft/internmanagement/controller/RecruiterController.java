package com.robosoft.internmanagement.controller;

import com.robosoft.internmanagement.model.*;
import com.robosoft.internmanagement.modelAttributes.Applications;
import com.robosoft.internmanagement.modelAttributes.AssignBoard;
import com.robosoft.internmanagement.modelAttributes.CandidateInvites;
import com.robosoft.internmanagement.service.EmailService;
import com.robosoft.internmanagement.service.MemberService;
import com.robosoft.internmanagement.service.RecruiterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/invite")
    public ResponseEntity<String> invites(@ModelAttribute CandidateInvites invites)
    {
        boolean result = emailService.sendInviteEmail(invites);

        if (result){
            return ResponseEntity.ok().body("Invite sent to " + invites.getCandidateName());
        }else {
            return ResponseEntity.status(HttpStatus.valueOf("Insufficient information")).build();
        }
    }

    @PutMapping("/verify-otp")
    public String verify(@RequestParam String emailId,@RequestParam String otp)
    {
        return emailService.verification(emailId,otp);
    }

    @GetMapping("/organizer")
    public ResponseEntity<List<Organizer>> getList()
    {
        List<Organizer> list = recruiterService.getOrganizer();
        if(list == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }else
            return ResponseEntity.of(Optional.of(list));
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
    public ResponseEntity<String> setOrganizer(@ModelAttribute AssignBoard assignBoard)
    {
        String result = recruiterService.assignOrganizer(assignBoard);
        if(result==null)
        {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }else
            return ResponseEntity.of(Optional.of(result));
    }

    @GetMapping("/get-assignboard")
    public List<AssignBoardPage> getPage()
    {
        return recruiterService.getAssignBoardPage();
    }

    @GetMapping("/rejected-cv")
    public List<RejectedCv> getCvPage()
    {
        return recruiterService.getRejectedCvPage();
    }
}
